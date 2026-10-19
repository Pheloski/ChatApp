package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    Login login = new Login();

    // Test data from the brief
    private static final String VALID_USERNAME = "kyl_1";
    private static final String INVALID_USERNAME = "kyle!!!!!!";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String INVALID_PASSWORD = "password";
    private static final String VALID_CELL = "+27838968976";
    private static final String INVALID_CELL = "08966553";

    // ---------- assertEquals: messages ----------

    @Test
    public void testUsernameCorrectlyFormattedMessage() {
        assertEquals("Username successfully captured.", login.usernameMessage(VALID_USERNAME));
    }

    @Test
    public void testUsernameIncorrectlyFormattedMessage() {
        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.",
                login.usernameMessage(INVALID_USERNAME));
    }

    @Test
    public void testPasswordMeetsComplexityMessage() {
        assertEquals("Password successfully captured.", login.passwordMessage(VALID_PASSWORD));
    }

    @Test
    public void testPasswordDoesNotMeetComplexityMessage() {
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, "
                + "and a special character.", login.passwordMessage(INVALID_PASSWORD));
    }

    @Test
    public void testCellPhoneCorrectlyFormattedMessage() {
        assertEquals("Cell number successfully captured.", login.cellPhoneMessage(VALID_CELL));
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedMessage() {
        assertEquals("Cell number is incorrectly formatted or does not contain an international "
                + "code; please correct the number and try again.",
                login.cellPhoneMessage(INVALID_CELL));
    }

    // ---------- assertEquals: registration ----------

    @Test
    public void testRegisterUserSuccess() {
        assertEquals("User has been registered successfully.",
                login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL));
    }

    @Test
    public void testRegisterUserFailedUsername() {
        assertEquals(login.usernameMessage(INVALID_USERNAME),
                login.registerUser(INVALID_USERNAME, VALID_PASSWORD, VALID_CELL));
    }

    @Test
    public void testRegisterUserFailedPassword() {
        assertEquals(login.passwordMessage(INVALID_PASSWORD),
                login.registerUser(VALID_USERNAME, INVALID_PASSWORD, VALID_CELL));
    }

    @Test
    public void testRegisterUserFailedCell() {
        assertEquals(login.cellPhoneMessage(INVALID_CELL),
                login.registerUser(VALID_USERNAME, VALID_PASSWORD, INVALID_CELL));
    }

    // ---------- assertEquals: login messages ----------

    @Test
    public void testLoginStatusSuccessMessage() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(VALID_USERNAME, VALID_PASSWORD, "Kyle", "Smith"));
    }

    @Test
    public void testLoginStatusFailedMessage() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(VALID_USERNAME, "wrongPass1!", "Kyle", "Smith"));
    }

    // ---------- assertTrue / assertFalse ----------

    @Test
    public void testLoginSuccessful() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);
        assertTrue(login.loginUser(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    public void testLoginFailed() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);
        assertFalse(login.loginUser(VALID_USERNAME, "wrongPass1!"));
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        assertTrue(login.checkUserName(VALID_USERNAME));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName(INVALID_USERNAME));
    }

    @Test
    public void testPasswordMeetsComplexity() {
        assertTrue(login.checkPasswordComplexity(VALID_PASSWORD));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        assertFalse(login.checkPasswordComplexity(INVALID_PASSWORD));
    }

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber(VALID_CELL));
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber(INVALID_CELL));
    }
}
