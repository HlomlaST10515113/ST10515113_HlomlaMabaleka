
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registrationlogin;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 *
 * @author hloml
 */
public class RegistrationLogin {
static Scanner input = new Scanner(System.in);

public static boolean checkUserName(String username){
    return username.contains("_") &&username.length() <= 5;
}

public static boolean checkPasswordComplexity(String password){
    String regex = "(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&].{8,}$)";
    return Pattern.matches(regex, password);
}

public static boolean checkCellPhoneNumber(String number){
    String regex = "\\+27\\d{9}$";
    return Pattern.matches(regex, number);
} 
 
public static String registerUser(String username, String Password){
    if (!checkUserName(username)){
        return "Usernameis not correct.Must have _ and a mac 6 chars,";
    }
    if (!checkPasswordComplexity(Password)){
        return "Password is not correct. Must have 8+ chars, capital letter, number , and special char,";
    }
    return "Username and password succesfully captured. User registered!";
}

public static boolean loginUser(String username, String password,
                                String storedUsername, String storedPassword){
    return username.equals(storedUsername) && password.equals(storedPassword); 
}

public static String returnLoginStatus(boolean status){
    if (status){
        return "Login successful!Welcome back!";
    }else{
        return "Username or password incorrect, please try again";
    }
}

    public static void main(String[] args) {
        
        String storedUsername = "";
        String storedPassword = "";
        String CellPhone;
        
        System.out.println(" Welcome to the login System");
        
        System.out.println(" Registration");
        String username;
        while (true) {
            System.out.println("Enter your username(must contain '_' and max 5 chars):");
            username = input.nextLine();
            if (checkUserName(username)) break;
            System.out.println("Invalid username. Example user_");
        }
        String password;
        while(true){
            System.out.println("Enter password (8+ chars, 1 capital, 1 number, 1 special char):");
            password = input.nextLine();
            if(checkPasswordComplexity(password))break;
            System.out.println("Invalid password.Example: password1!\n");
        }
    //String username = null;
        String registrationMessage = RegistrationLogin.registerUser(username, password);
        System.out.println(registrationMessage);
        storedUsername = username;
        storedPassword = password;
        
         while(true){
            System.out.println("Enter Cell Phone (+27 followed by 9 didgits):");
            CellPhone = input.nextLine();
            if (checkCellPhoneNumber(CellPhone)){
                System.out.println("Cell phone number successfully add.");
                break;
            }
            System.out.println("Invalid number. Must start with+27 and have 9 other digits. Example: +27123456789\n");
        }
        System.out.println("\nLogin");
        
        String loginUser;
        String loginPass;
        boolean status;
        
        while (true){
            System.out.println("Enter Username: ");
            loginUser = input.nextLine();
            
            System.out.println("Enter Password ");
            loginPass = input.nextLine();
            
            status = RegistrationLogin.loginUser(loginUser, loginPass,storedUsername, storedPassword);
            System.out.println(RegistrationLogin.returnLoginStatus(status));
            
            if (status) break;
            System.out.println("Pleasetry again.\n");
        }
        System.out.println("\n You are now logged in.");
    }
}
