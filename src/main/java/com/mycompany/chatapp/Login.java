package com.mycompany.chatapp;

public class Login {
    
    private String storedUsername;
    private String storedPassword;
    private String storedCell;

    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        boolean hasCapital = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?-].*");
        boolean longEnough = password.length() >= 8;
        
        return hasCapital && hasNumber && hasSpecial && longEnough;
    }

    public boolean checkCellPhoneNumber(String cell) {
        // Must start with +27 and be 12 chars total: +27 + 9 digits = 12? 
        // Example: +27831234567 = 12 chars
        return cell.startsWith("+27") && cell.length() == 12 && cell.matches("\\+27[0-9]{9}");
    }

    public String registerUser(String username, String password, String cell) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number and a special character.";
        }
        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number is incorrectly formatted or does not contain an international code, please correct the number to start with +27.";
        }
        
        this.storedUsername = username;
        this.storedPassword = password;
        this.storedCell = cell;
        
        return "User has been registered successfully.";
    }

    public boolean loginUser(String username, String password) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    public String returnLoginStatus(String username, String password, String firstName, String lastName) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}