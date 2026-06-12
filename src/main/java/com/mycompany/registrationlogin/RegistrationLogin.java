//HLOMLA VUYO MABALEKA ST10515113 
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.registrationlogin;

import java.util.ArrayList;
import java.util.Scanner;        // import utilities
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.text.SimpleDateFormat; // import date format
import java.io.FileWriter;         // import file writer
import java.io.IOException;        // import IO error handling
import java.util.regex.Pattern;    // import regex


/**
 *
 * @author hloml
 */

// Define a Message class to store message attributes
class Message {
    String id;        // 10-digit unique identifier
    String hash;      // generated hash code
    String recipient; // recipient phone number or developer ID
    String content;   // message body (max 250 chars)
    String timestamp; // date and time message was created
    String status;    // "Sent", "Stored", or "Disregard"

    /** Constructs a fully-populated Message object. */
    public Message(String id, String hash, String recipient,
                   String content, String timestamp, String status) {
        this.id        = id;
        this.hash      = hash;
        this.recipient = recipient;
        this.content   = content;
        this.timestamp = timestamp;
        this.status    = status;
    }
}

public class RegistrationLogin { // main class

    // Part 3: Five Parallel 1D Arrays (populated via methods, no hard-coding)
    static ArrayList<String> sentMessages      = new ArrayList<>();
    static ArrayList<String> disregardMessages = new ArrayList<>();
    static ArrayList<String> storedMessages    = new ArrayList<>();
    static ArrayList<String> messageHashes     = new ArrayList<>();
    static ArrayList<String> messageIDs        = new ArrayList<>();

    // Master list of full Message objects used for display, search, and delete
    static ArrayList<Message> messages = new ArrayList<>();

    static Scanner input        = new Scanner(System.in);
    static int sentCount        = 0;  // running total of Sent messages
    static int messageLimit     = 0;  // user-defined cap on messages to send
    static int messageCounter   = 0;  // counts how many messages have been composed

    // Path to the JSON file used for saving and reading messages
    static final String JSON_FILE = "messages.json";


    
    // VALIDATION METHODS
    // Check if username contains underscore and is max 5 chars
    public static boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // Check password has 8+ chars, 1 uppercase, 1 digit, 1 special char
    public static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(regex, password);
    }

    // Check if cell phone number is valid (+27 followed by 9 digits)
    public static boolean checkCellPhoneNumber(String number) {
        String regex = "^\\+27\\d{9}$";
        return Pattern.matches(regex, number);
    }

    // Validate registration credentials and return feedback message
    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is not correct. Must have _ and max 5 chars.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correct. Must have 8+ chars, capital letter, number, and special char.";
        }
        return "Username and password successfully captured. User registered!";
    }

    // Check login credentials against stored credentials
    public static boolean loginUser(String username, String password,
                                    String storedUsername, String storedPassword) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    // Show login message
    public static String returnLoginStatus(boolean status) {
        if (status) {
            return "Login successful! Welcome back!";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // Validate recipient phone number
    public static String validateNumber(String num) {
        if (num == null) return "incorrect";                           // null check
        if (!num.startsWith("+27"))                                    // must start +27
            return "incorrect: Recipient number must start with +27";
        if (num.length() != 12)                                        // must be 12 chars
            return "incorrect: Recipient number must be 12 digits (+27XXXXXXXXX)";
        if (!num.substring(3).matches("\\d{9}"))                      // rest must be digits
            return "incorrect: only digits allowed after +27";
        return "Recipient number entered successfully";
    }

    // Generate message hash from ID, sequence number, and first/last word of message
    public static String createMessageHash(String id, int num, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0 ? words[0] : "MSG";
        String last  = words.length > 1 ? words[words.length - 1] : words[0];
        return (id.substring(0, 2) + ":" + num + ":" + first + last).toUpperCase();
    }



    // MAIN METHOD
    
    public static void main(String[] args) {
        String[] creds = registerUser();
        login(creds);
        messageLimit = getMessageLimit();

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1": sendMessage();        break;
                case "2": showMessages();       break;
                case "3": discardLastMessage(); break;
                case "4": storedMessages();     break;
                case "5": saveAndExit(); running = false; break;
                default:  System.out.println("Invalid option. Choose 1-5.");
            }
        }
    }


   
    // PARALLEL ARRAY HELPERS
     /**
     * Adds one message to ALL parallel arrays and the master messages list.
     * This keeps every array in sync at all times.
     * @param id        10-digit message ID string
     * @param recipient phone number or developer ID
     * @param content   message body text
     * @param status    "Sent", "Stored", or "Disregard"
     */
     
    public static void addMessageToArrays(String id, String recipient,
                                          String content, String status) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String hash      = createMessageHash(id, messages.size(), content);

        Message msg = new Message(id, hash, recipient, content, timestamp, status);
        messages.add(msg);
        messageIDs.add(id);
        messageHashes.add(hash);

        if (status.equals("Sent")) {
            sentMessages.add(content);
            sentCount++;
        } else if (status.equals("Stored")) {
            storedMessages.add(content);
        } else if (status.equals("Disregard")) {
            disregardMessages.add(content);
        } else {
            System.out.println("Unknown status '" + status + "' for message: " + id);
        }
    }

    /**
     * Removes a message from the master list AND all relevant parallel arrays.
     * Ensures no orphaned data remains in any array.
     * @param m the Message object to remove
     */
    public static void removeMessageFromArrays(Message m) {
        messages.remove(m);
        messageIDs.remove(m.id);
        messageHashes.remove(m.hash);

        if (m.status.equals("Sent")) {
            sentMessages.remove(m.content);
            sentCount--;
        } else if (m.status.equals("Stored")) {
            storedMessages.remove(m.content);
        } else if (m.status.equals("Disregard")) {
            disregardMessages.remove(m.content);
        }
    }


    // REGISTRATION AND LOGIN
    // Method to register a new user and return their credentials
    public static String[] registerUser() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("               Registration                ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        String username;
        while (true) {
            System.out.print("Enter Username (must contain '_' and max 5 chars): ");
            username = input.nextLine();
            if (checkUserName(username)) break;
            System.out.println("Invalid username. Example: User_");
        }
        String password;
        while (true) {
            System.out.print("Enter Password (8+ chars, 1 capital, 1 number, 1 special char): ");
            password = input.nextLine();
            if (checkPasswordComplexity(password)) break;
            System.out.println("Invalid password. Example: Password5!");
        }
        String cellPhone;
        while (true) {
            System.out.print("Enter Cell Phone (+27 followed by 9 digits): ");
            cellPhone = input.nextLine();
            if (checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }
            System.out.println("Invalid number. Must start with +27 and have 9 digits. Example: +27123456789");
        }

        // Display registration message and return credentials
        System.out.println(registerUser(username, password));
        return new String[]{username, password};
    }

    // Method to handle user login
    public static void login(String[] credentials) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("                Login                     ");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        while (true) {
            System.out.print("Enter Username: ");
            String loginUser = input.nextLine();
            System.out.print("Enter Password: ");
            String loginPass = input.nextLine();
            if (loginUser(loginUser, loginPass, credentials[0], credentials[1])) {
                
                System.out.println("Welcome back " + loginUser + ", great to see you again.");
                System.out.println("\n");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println("          WELCOME TO QUICKCHAT            ");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                break; // Break loop if login is successful
            }
            System.out.println("Invalid credentials. Try again.");
        }
    }

    // Method to get the message limit
    public static int getMessageLimit() {
        while (true) {
            try {
                System.out.print("How many messages do you want to send? ");
                int limit = Integer.parseInt(input.nextLine().trim());
                if (limit <= 0) {
                    System.out.println("Please enter a number greater than 0.");
                } else {
                    return limit;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    // Method to display the menu
    public static void displayMenu() {
        System.out.println("\n~~~~~~~ QUICKCHAT MENU ~~~~~~~");
        System.out.println(" 1. Send Message");
        System.out.println(" 2. Show recently sent messages");
        System.out.println(" 3. Discard Last Message");
        System.out.println(" 4. Stored Messages");
        System.out.println(" 5. Quit");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.print("Choose an option (1-5): ");
    }

    // Increments the message counter each time a new message is composed
    public static void composeMessage() {
        messageCounter++;
    }



    // SEND MESSAGE    
    public static void sendMessage() {
        if (sentCount >= messageLimit) {
            System.out.println("[WARNING] Message limit reached!");
            return;
        }

        // FIX BUG 4: call composeMessage() to increment the counter
        composeMessage();
        System.out.println("\n~~~~ Message No." + messageCounter + "~~~~");

        // Validate recipient number
        System.out.print("Enter Recipient Number (+27XXXXXXXXX): ");
        String rec = input.nextLine().trim();
        String validation = validateNumber(rec);

        // FIX BUG 1: condition was inverted — blocked valid numbers, allowed invalid ones
        if (!validation.equals("Recipient number entered successfully")) {
            System.out.println("ERROR- " + validation);
            return;
        }

        // Validate recipient username
        String usernameUser;
        while (true) {
            System.out.print("Enter Recipient Username (must contain '_' and max 5 chars): ");
            usernameUser = input.nextLine();
            if (checkUserName(usernameUser)) break;
            System.out.println("Invalid Recipient username. Example: User_");
        }

        // Validate message length
        System.out.print("Enter your message (max 250 characters): ");
        String msg = input.nextLine();
        if (msg.length() > 250) {
            int excessChars = msg.length() - 250;
            int excessWords = msg.substring(250).split("\\s+").length;
            System.out.println("Message exceeds 250 characters by " + excessChars
                    + " characters (" + excessWords + " words). [Please reduce the size]");
            return;
        }

        // FIX BUG 3 and BUG 10: print the menu FIRST, then read the user's choice
        System.out.println("\nWhat would you like to do with this message?");
        System.out.println(" 1. Send Message");
        System.out.println(" 2. Store Message");
        System.out.println(" 3. Disregard Message");
        System.out.print("Choose an option (1-3): ");
        String statusChoice = input.nextLine().trim();

        // Map choice to status string used by addMessageToArrays
        String status;
        if (statusChoice.equals("1")) {
            status = "Sent";
        } else if (statusChoice.equals("2")) {
            status = "Stored";
        } else if (statusChoice.equals("3")) {
            status = "Disregard";
        } else {
            System.out.println("Invalid choice. Defaulting to Sent.");
            status = "Sent";
        }

        // Generate unique 10-digit ID
        String id = String.format("%010d", (long)(Math.random() * 10000000000L));

        // FIX BUG 2: call addMessageToArrays() so ALL 5 parallel arrays stay in sync
        addMessageToArrays(id, rec, msg, status);

        // Retrieve the message we just added to display its hash and timestamp
        Message added = messages.get(messages.size() - 1);

        System.out.println("\n...............................");
        System.out.println("MESSAGE ID         : " + added.id);
        System.out.println("HASH               : " + added.hash);
        System.out.println("RECIPIENT NUMBER   : " + rec);
        System.out.println("RECIPIENT USERNAME : " + usernameUser);
        System.out.println("MESSAGE            : " + msg);
        System.out.println("STATUS             : " + status);
        System.out.println("TIME               : " + added.timestamp);
        System.out.println("...............................");
        System.out.println("Message successfully " + status + ".");
    }


    
    // DISCARD LAST MESSAGE
   // Method to discard the last message
    public static void discardLastMessage() {
        if (messages.isEmpty()) {
            System.out.println("No messages to discard.");
            return;
        }
        // FIX BUG 5: use removeMessageFromArrays() to keep ALL parallel arrays in sync
        Message lastMessage = messages.get(messages.size() - 1);
        removeMessageFromArrays(lastMessage);
        System.out.println("Message discarded: " + lastMessage.content);
    }


    // SHOW MESSAGES
    // Method to show messages — prints full details of every message in the master list
    public static void showMessages() {
        System.out.println("\n~~~~~~~ALL MESSAGES~~~~~~~");
        if (messages.isEmpty()) {
            System.out.println("No messages found.");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
            return;
        }

        int sentLocalCount       = 0;
        int storedLocalCount     = 0;
        int disregardedLocalCount = 0;

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);

            //  switch on toLowerCase() so cases must also be lowercase
            switch (msg.status.toLowerCase()) {
                case "sent":      sentLocalCount++;        break;
                case "stored":    storedLocalCount++;      break;
                case "disregard": disregardedLocalCount++; break;
            }

            System.out.println("ID       : " + msg.id);
            System.out.println("HASH     : " + msg.hash);
            System.out.println("RECIPIENT: " + msg.recipient);
            System.out.println("MESSAGE  : " + msg.content);
            System.out.println("STATUS   : " + msg.status);
            System.out.println("TIME     : " + msg.timestamp);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        System.out.println("\nTotal: " + messages.size()
                + "  |  Sent: " + sentLocalCount
                + "  |  Stored: " + storedLocalCount
                + "  |  Disregarded: " + disregardedLocalCount);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }


    // SEARCH MESSAGE (by ID or Recipient)
    // Prompts user to search by Message ID or Recipient Number
    public static void searchMessage() {
        System.out.println("\n~~~~~~SEARCH~~~~~~");
        System.out.println("1. By Message ID");
        System.out.println("2. By Recipient Number");
        System.out.print("Choose: ");
        String sc = input.nextLine().trim();
        ArrayList<Message> results = new ArrayList<>();

        if (sc.equals("1")) {
            System.out.print("Enter Message ID: ");
            String sid = input.nextLine().trim();
            for (Message m : messages) {
                if (m.id.equalsIgnoreCase(sid)) results.add(m);
            }
        } else if (sc.equals("2")) {
            System.out.print("Enter Recipient Number: ");
            String srec = input.nextLine().trim();
            for (Message m : messages) {
                if (m.recipient.equalsIgnoreCase(srec)) results.add(m);
            }
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No matching messages found.");
        } else {
            System.out.println("Results (" + results.size() + "):");
            for (Message m : results) {
                System.out.println("  [" + m.status + "] " + m.recipient + " - " + m.content);
            }
        }
    }


    // DELETE MESSAGE BY ID
    // Prompts for a message ID, confirms with the user, then deletes it
    public static void deleteMessageById() {
        if (messages.isEmpty()) {
            System.out.println("INFO No messages to delete.");
            return;
        }
        System.out.print("Enter Message ID to delete: ");
        String did = input.nextLine().trim();
        Message target = findById(did);
        if (target == null) {
            System.out.println("[ERROR] No message found with ID: " + did);
            return;
        }
        System.out.println("Found: [" + target.status + "] " + target.content);
        System.out.print("Confirm delete? (yes/no): ");
        if (input.nextLine().trim().equalsIgnoreCase("yes")) {
            removeMessageFromArrays(target);
            System.out.println("SUCCESS Message deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // Linear search for a message by its ID field
    public static Message findById(String id) {
        for (Message m : messages) {
            if (m.id.equalsIgnoreCase(id)) return m;
        }
        return null;
    }

    // Linear search for a message by its hash field
    public static Message findByHash(String hash) {
        for (Message m : messages) {
            if (m.hash.equalsIgnoreCase(hash)) return m;
        }
        return null;
    }


    // PART 3 - STORED MESSAGES SUB-MENU (Options a to f)
    // Displays the Stored Messages sub-menu and handles option selection
    
    public static void storedMessages() {
        boolean inSubMenu = true;
        while (inSubMenu) {
            System.out.println("...............................................................");
            System.out.println("\n                  STORED MESSAGES MENU                      ");
            System.out.println("...............................................................");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message by message ID");
            System.out.println("d. Search message by recipient number");
            System.out.println("e. Delete a message by using message hash");
            System.out.println("f. Display full message report");
            System.out.println("o. Back to main menu");
            System.out.println(".............................................");
            System.out.print("Choose (a-f or o): ");
            String ch = input.nextLine().trim().toLowerCase();
            switch (ch) {
                case "a": displayStoredSenderRecipient(); break;
                case "b": displayLongestMessage();        break;
                case "c": searchByMessageID();            break;
                case "d": searchByRecipient();            break;
                case "e": deleteByHash();                 break;
                case "f": displayFullReport();            break;
                case "o": inSubMenu = false;              break;
                default:  System.out.println(" Invalid option.");
            }
        }
    }

    // a. Display sender and recipient of all stored messages
    public static void displayStoredSenderRecipient() {
        System.out.println("\n~~~~Stored Messages: Recipient and Preview~~~~");
        boolean found = false;
        for (Message m : messages) {
            if (m.status.equals("Stored")) {
                String preview = m.content.length() > 40
                        ? m.content.substring(0, 40) + "..." : m.content;
                System.out.println("Recipient : " + m.recipient
                        + "  |  Message: " + preview);
                found = true;
            }
        }
        // FIX BUG 7: was if(found) — inverted logic showed "not found" when there WERE messages
        if (!found) {
            System.out.println("No stored messages found.");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    // b. Display the longest message (across all arrays)
    // Returns the content of the longest message, or empty string if no messages
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : messages) {
            if (m.content.length() > longest.length()) {
                longest = m.content;
            }
        }
        if (longest.isEmpty()) {
            System.out.println("No messages available.");
        } else {
            System.out.println("\n~~~ Longest Message ~~~");
            System.out.println(longest);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        return longest;
    }

    // c. Search for a message ID - display recipient and message
    public static String searchByMessageID() {
        System.out.print("Enter Message ID or Developer Number to search: ");
        return searchByMessageIDLogic(input.nextLine().trim());
    }

    // Searches messages by ID field; also checks recipient field
    // to support the "developer number" test case from the POE spec
    public static String searchByMessageIDLogic(String query) {
        for (Message m : messages) {
            if (m.id.equalsIgnoreCase(query) || m.recipient.equalsIgnoreCase(query)) {
                System.out.println("Recipient : " + m.recipient);
                System.out.println("Message   : " + m.content);
                return m.content;
            }
        }
        System.out.println("No message found for: " + query);
        return "";
    }

    // d. Search all messages for a particular recipient
    public static ArrayList<String> searchByRecipient() {
        System.out.print("Enter Recipient Number: ");
        return searchByRecipientLogic(input.nextLine().trim());
    }

    // Returns all message bodies matching the given recipient across all arrays
    public static ArrayList<String> searchByRecipientLogic(String recipient) {
        ArrayList<String> results = new ArrayList<>();
        System.out.println("\n~~~~ Messages for recipient: " + recipient + " ~~~~~");
        for (Message m : messages) {
            if (m.recipient.equalsIgnoreCase(recipient)) {
                System.out.println("[" + m.status + "] " + m.content);
                results.add(m.content);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No messages found for this recipient.");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        return results;
    }

    // e. Delete a message using the message hash
    public static boolean deleteByHash() {
        System.out.print("Enter Message Hash to delete: ");
        return deleteByHashLogic(input.nextLine().trim());
    }

    // Finds a message by hash, removes it from all arrays, and reports success
    // Returns true if deletion succeeded, false if hash not found
    public static boolean deleteByHashLogic(String hash) {
        Message target = findByHash(hash);
        if (target == null) {
            System.out.println("No message found with hash: " + hash);
            return false;
        }
        System.out.println("Deleting: \"" + target.content + "\"");
        removeMessageFromArrays(target);
        System.out.println("Message \"" + target.content + "\" successfully deleted.");
        return true;
    }

    // f. Display full report — shows Message Hash, Recipient, Status, and Message
    public static void displayFullReport() {
        System.out.println("\n.......... FULL MESSAGE REPORT ...........");
        if (messages.isEmpty()) {
            System.out.println("No messages available.");
            System.out.println("........................................");
            return;
        }
        System.out.printf("%-20s %-16s %-10s %s%n",
                "Message Hash", "Recipient", "Status", "Message");                                                                                                                                                                                                                                                                                                                                                                                                                                            
        System.out.println("...............................................................");
        for (Message m : messages) {
            String preview = m.content.length() > 35
                    ? m.content.substring(0, 35) + "..." : m.content;
            System.out.printf("%-20s %-16s %-10s %s%n",
                    m.hash, m.recipient, m.status, preview);
        }
        System.out.println("................................................................");
        System.out.println("Sent: " + sentMessages.size()
                + " | Stored: " + storedMessages.size()
                + " | Disregarded: " + disregardMessages.size());
        System.out.println("...........................................");
    }


    
    // PART 3 - READ JSON FILE INTO ARRAY    
    // Reads the saved messages.json file line by line and loads message bodies
    // into the storedMessages parallel array. No external JSON library is needed.
    
    public static void readJsonIntoArray() {
        System.out.println("\n~~~~~Reading JSON file into array~~~~~");
        ArrayList<String> loadedFromFile = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE))) {
            String line;
            String currentMessage = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Extract content from lines like: "message": "Hello world",
                if (line.startsWith("\"message\"")) {
                    int colonIndex = line.indexOf(":");
                    if (colonIndex != -1) {
                        String valueSection = line.substring(colonIndex + 1).trim();
                        // Remove surrounding quotes and trailing comma if present
                        if (valueSection.startsWith("\"")) {
                            valueSection = valueSection.substring(1);
                        }
                        if (valueSection.endsWith("\",")) {
                            valueSection = valueSection.substring(0, valueSection.length() - 2);
                        } else if (valueSection.endsWith("\"")) {
                            valueSection = valueSection.substring(0, valueSection.length() - 1);
                        }
                        currentMessage = valueSection;
                    }
                }

                // When a record closes, save the extracted message
                if (line.startsWith("}") && currentMessage != null) {
                    loadedFromFile.add(currentMessage);
                    if (!storedMessages.contains(currentMessage)) {
                        storedMessages.add(currentMessage);
                    }
                    currentMessage = null;
                }
            }

            if (loadedFromFile.isEmpty()) {
                System.out.println("JSON file is empty or contains no messages.");
            } else {
                System.out.println("SUCCESS " + loadedFromFile.size()
                        + " message(s) loaded from " + JSON_FILE + ":");
                for (int i = 0; i < loadedFromFile.size(); i++) {
                    System.out.println("  [" + (i + 1) + "] " + loadedFromFile.get(i));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read '" + JSON_FILE
                    + "'. Please save messages first (option 5 -> yes).");
        }
        System.out.println("...................................................");
    }


    // SAVE MESSAGES TO JSON FILE
    // Serialises every message in the master list to a properly formatted JSON file.
    // FIX BUG 8 + 9: replaced invalid backtick separators with correct JSON structure
    // so that readJsonIntoArray() can correctly parse the file back
    public static void storeMessage() {
        if (messages.isEmpty()) {
            System.out.println("[INFO] No messages to save.");
            return;
        }
        try (FileWriter fw = new FileWriter(JSON_FILE)) {
            fw.write("[\n");
            for (int i = 0; i < messages.size(); i++) {
                Message m = messages.get(i);
                fw.write("  {\n");
                fw.write("    \"id\": \""        + m.id        + "\",\n");
                fw.write("    \"hash\": \""       + m.hash      + "\",\n");
                fw.write("    \"recipient\": \""  + m.recipient + "\",\n");
                fw.write("    \"message\": \""    + m.content   + "\",\n");
                fw.write("    \"status\": \""     + m.status    + "\",\n");
                fw.write("    \"time\": \""       + m.timestamp + "\"\n");
                fw.write("  }" + (i < messages.size() - 1 ? "," : "") + "\n");
            }
            fw.write("]");
            System.out.println("SUCCESS " + messages.size()
                    + " message(s) saved to " + JSON_FILE + ".");
        } catch (IOException e) {
            System.out.println("Could not write file: " + e.getMessage());
        }
    }


    // SAVE AND EXIT
    // Method to save and exit
    public static void saveAndExit() {
        String save;
        while (true) {
            System.out.print("Save before exit? (yes/no): "); // ask
            save = input.nextLine();                          // read input
            if (save.equalsIgnoreCase("yes")) {              // if yes
                storeMessage();                               // save messages
                System.out.println("SUCCESS Messages saved before exit.");
                break;
            } else if (save.equalsIgnoreCase("no")) {        // if no
                break;
            } else {
                System.out.println("Invalid input! Please type 'yes' or 'no'.");
            }
        }
        System.out.println("Total messages sent: " + sentCount); // show total
        System.out.println("                                           ");
        System.out.println("                 Goodbye!             "); // exit msg
        System.out.println("                                        ");

    }
}
