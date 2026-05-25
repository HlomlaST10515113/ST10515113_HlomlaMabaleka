//HLOMLA VUYO MABALEKA ST10515113 
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registrationlogin;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author hloml
 */
// Define a Message class to store message attributes
class Message {
    String id;
    String hash;
    String recipient;
    String content;
    String timestamp;
    String status;
// Constructor to initialize a Message object
    public Message(String id, String hash, String recipient, String content, String timestamp, String status) {
        this.id = id;
        this.hash = hash;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.status = status;
    }
}


public class RegistrationLogin {
// Stores all messages created during the session
    static ArrayList<Message> messages = new ArrayList<>();
// Used to read input from the user
    static Scanner input = new Scanner(System.in);
//Keeps track of how many messages have been sent 
    static int sentCount = 0;
// Maximum number of messages allowed
    static int messageLimit = 0;
    
    
    
// Validation methods...
    public static boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }
    public static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.matches(regex, password);
    }
// Check if cell phone number is valid (+27 followed by 9 digits)
    public static boolean checkCellPhoneNumber(String number) {
        String regex = "^\\+27\\d{9}$";
        return Pattern.matches(regex, number);
    }
    public static String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return "Username is incorrect. Must have _ and max 5 chars.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is incorrect. Must have 8+ chars, capital letter, number, and special char.";
        }
        return "Username and password correctly captured. User registered!";
    }
    public static boolean loginUser(String username, String password, String storedUsername, String storedPassword) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }
    
    // Show login message
    public static String returnLoginStatus(boolean status) {
        if (status) {
            return "Correct login ! Welcome back!";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
    
       public static String validateNumber(String num) { // check phone number

        if (num == null) return "Invalid"; // null check

        if (!num.startsWith("+27")) // must start +27
            return "Invalid: must start with +27";

        if (num.length() != 12) // must be 12 chars
            return "Invalid: must be 12 digits (+27XXXXXXXXX)";

        if (!num.substring(3).matches("\\d{9}")) // rest must be digits
            return "Invalid: only digits allowed after +27";

        return "Valid"; // correct number
    }
    
    
    public static String createMessageHash(String id, int num, String msg) {
        String[] words = msg.trim().split(" ");
        String first = words.length > 0 ? words[0] : "MSG";
        String last = words.length > 1 ? words[words.length - 1] : words[0];
        return (id.substring(0, 2) + ":" + num + ":" + first + last).toUpperCase();
    }
    
    
    
 // Main method
    public static void main(String[] args) {
    String[] credentials = registerUser();
    login(credentials);
    
// Get the message limit
        messageLimit = getMessageLimit();
        
// Loop until the user chooses to quit
        while (true) {
                // Display the menu
            displayMenu();
                // Get the user's choice
            String choice = input.nextLine();
            switch (choice) {
                case "1":
                // Send a message           
                    sendMessage();
                    break;
                case "2":
                // Show messages
                    showMessages();
                    break;
                case "3":
                // Discard the last message             
                    discardLastMessage();
                    break;
                case "4":
                // Save messages to file   
                    storeMessage();
                    break;
                case "5":
                // Save and exit    
                    saveAndExit();
                    return;
                default:
                // Incorrect option
                    System.out.println("[ERROR] Incorrect option!");
            }
        }
    }
    
    
    
// Method to register a new user and return their credentials
    public static String[] registerUser() {
    System.out.println("Registration");
    String username;
    while (true) {
        System.out.print("Enter Username (must contain '_' and max 5 chars): ");
        username = input.nextLine();
        if (checkUserName(username)) break;
        System.out.println("Incorrect username. Example: User_");
    }
    String password;
    while (true) {
        System.out.print("Enter Password (8+ chars, 1 capital, 1 number, 1 special char): ");
        password = input.nextLine();
        if (checkPasswordComplexity(password)) break;
        System.out.println("Incorrect password. Example: Password5!");
    }
    String cellPhone;
     while (true) {
            System.out.print("Enter Cell Phone (+27 followed by 9 digits): ");
            cellPhone = input.nextLine();
            if (checkCellPhoneNumber(cellPhone)) {
                System.out.println("Cell phone number successfully added.");
                break;
            }  
            }
            System.out.println("Incorrect number. Must start with +27 and have 9 digits. Example: +27123456789");
     
    
  
// Display registration message and return credentials
    System.out.println(registerUser(username, password));
    return new String[] {username, password};
     }     
     
// Method to handle user login
public static void login(String[] credentials) {
    System.out.println("\nLogin");
    while (true) {
        System.out.print("Enter Username: ");
        String loginUser = input.nextLine();
        System.out.print("Enter Password: ");
        String loginPass = input.nextLine();
        if (loginUser(loginUser, loginPass, credentials[0], credentials[1])) {
            System.out.println("Welcome to QuickChat");
            break;// Break loop if login is successful
        }
        System.out.println("Incorrect credentials. Try again.");
    }
}



// Method to get the message limit
    public static int getMessageLimit() {
        while (true) {
            try {
                System.out.print("How many messages do you want to send? ");
                return Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Incorrect number!");
            }
        }
    }
    
    
    
// Method to display the menu
    public static void displayMenu() {
        System.out.println("\n~~~~MENU~~~~");
        System.out.println("1. Send Message");
        System.out.println("2. Show Messages");
        System.out.println("3. Discard Last Message");
        System.out.println("4. Save Messages");
        System.out.println("5. Quit");
        System.out.println("~~~~~~~~~~~~~~");
        System.out.print("Choose option: ");
    }
    
    
    
// Method to send a message
    public static void sendMessage() {
        if (sentCount >= messageLimit) {
            System.out.println("[WARNING] Message limit reached!");
            return;
        }
        System.out.print("Enter Recipient Number (+27XXXXXXXXX): ");
        String rec = input.nextLine();
        
        String validation = validateNumber(rec); // validate number
        if (!validation.equals("Valid")) { // if incorrect
            System.out.println("[ERROR] " + validation); // show error
            return;
        }
    
        System.out.print("Enter Message (max 250 chars): ");
        String msg = input.nextLine();
        if (msg.length() > 250) {
            System.out.println("[ERROR] Message exceed 250 characters![Pleas reduce the size]");
            return;
        }
        String id = String.format("%010d", (long) (Math.random() * 10000000000L));
        String hash = createMessageHash(id, sentCount, msg);
        Message message = new Message(id, hash, rec, msg, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "SENT");
        messages.add(message);
        sentCount++;
        System.out.println("\n[SUCCESS] Message successfully sent");
        System.out.println("ID : " + id);
        System.out.println("HASH : " + hash);
        System.out.println("TIME : " + message.timestamp);
        System.out.println("DATA: " + msg + " | Recipient Number: " + rec);
    }
    
    
    
   
// Method to show messages
    public static void showMessages() {
        System.out.println("\n..............................");
        System.out.println(" SHOW MESSAGES");
        System.out.println("................................");
        
        System.out.println("[COMING SOON] This feature is under development."); // placeholder
        System.out.println("Please check back later."); // note
        
        System.out.println("\n...............................");
    }
    
    
    
// Method to discard the last message
    public static void discardLastMessage() {
        if (messages.isEmpty()) {
            System.out.println("[INFO] No messages to discard.");
            return;
        }
        Message lastMessage = messages.remove(messages.size() - 1);
        sentCount--;
        System.out.println("[SUCCESS] Message discarded: " + lastMessage.content);
    }
    
    
    
// Method to save messages to file
    public static void storeMessage() {
        try (FileWriter file = new FileWriter("messages.json")) {
            file.write("[\n");
            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                file.write(" {\n");
                file.write(" \"id\": \"" + msg.id + "\",\n");
                file.write(" \"hash\": \"" + msg.hash + "\",\n");
                file.write(" \"recipient\": \"" + msg.recipient + "\",\n");
                file.write(" \"message\": \"" + msg.content + "\",\n");
                file.write(" \"status\": \"" + msg.status + "\",\n");
                file.write(" \"time\": \"" + msg.timestamp + "\"\n");
                file.write(" }" + (i < messages.size() - 1 ? "," : "") + "\n");
            }
            file.write("]");
            System.out.println("\n[SUCCESS] Messages successfully stored.");
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to save file!");
        }
    }
    
    
    
// Method to save and exit
    public static void saveAndExit() {
        String save;
        while (true){
        System.out.print("Save before exit? (yes/no): "); // ask
        save = input.nextLine(); // read input
        if (save.equalsIgnoreCase("yes")) { // if yes
            storeMessage(); // save messages
            break;
        } else if (save.equalsIgnoreCase("no")) { // if no
            break;
        } else {
            System.out.println("[ERROR] Invalid input! Please type 'yes' or 'no'."); // handle invalid input
        }
    }
    System.out.println("Total messages sent: " + sentCount); // show total
    System.out.println("Goodbye!"); // exit msg
    return; // end program
    }

    
}

