package com.mycompany.chatapp;

import java.util.regex.Pattern;

public class Login {

    private static final String USERNAME_SUCCESS = "Username successfully captured.";
    private static final String USERNAME_ERROR = "Username is not correctly formatted; please ensure "
            + "that your username contains an underscore and is no more than five characters in length.";

    private static final String PASSWORD_SUCCESS = "Password successfully captured.";
    private static final String PASSWORD_ERROR = "Password is not correctly formatted; please ensure "
            + "that the password contains at least eight characters, a capital letter, a number, "
            + "and a special character.";

    private static final String CELL_SUCCESS = "Cell number successfully captured.";
    private static final String CELL_ERROR = "Cell number is incorrectly formatted or does not contain "
            + "an international code; please correct the number and try again.";

    /*
     * Cell phone regex: the international code +27 followed by no more than
     * ten digits. The approach of validating a South African number with a
     * regular expression was researched from:
     * Stack Overflow (n.d.) Simple regex for South African phone number.
     * Available at: https://stackoverflow.com/questions/19322685/simple-regex-for-south-african-phone-number
     */
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+27\\d{1,10}$");

    private String storedUsername;
    private String storedPassword;
    private String storedCell;

    /** A username must contain an underscore and be no more than five characters long. */
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    /** A password needs 8+ characters, a capital letter, a number and a special character. */
    public boolean checkPasswordComplexity(String password) {
        boolean hasCapital = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?-].*");
        boolean longEnough = password.length() >= 8;

        return hasCapital && hasNumber && hasSpecial && longEnough;
    }

    /** A cell number needs the international code +27 followed by no more than ten digits. */
    public boolean checkCellPhoneNumber(String cell) {
        return CELL_PATTERN.matcher(cell).matches();
    }

    /** Returns the success or error message for a username. */
    public String usernameMessage(String username) {
        return checkUserName(username) ? USERNAME_SUCCESS : USERNAME_ERROR;
    }

    /** Returns the success or error message for a password. */
    public String passwordMessage(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_SUCCESS : PASSWORD_ERROR;
    }

    /** Returns the success or error message for a cell phone number. */
    public String cellPhoneMessage(String cell) {
        return checkCellPhoneNumber(cell) ? CELL_SUCCESS : CELL_ERROR;
    }

    public String registerUser(String username, String password, String cell) {
        if (!checkUserName(username)) {
            return USERNAME_ERROR;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_ERROR;
        }
        if (!checkCellPhoneNumber(cell)) {
            return CELL_ERROR;
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
