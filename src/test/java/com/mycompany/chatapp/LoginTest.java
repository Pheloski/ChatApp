package com.mycompany.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    Login login = new Login();

    @Test
    public void testUserNameCorrectlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUserNameIncorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexity() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneCorrect() {
        assertTrue(login.checkCellPhoneNumber("+27831234567"));
    }

    @Test
    public void testCellPhoneIncorrect() {
        assertFalse(login.checkCellPhoneNumber("0831234567"));
    }

    @Test
    public void testRegisterUserSuccess() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27831234567");
        assertEquals("User has been registered successfully.", result);
    }

    @Test
    public void testRegisterUserFailedUsername() {
        String result = login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27831234567");
        assertTrue(result.contains("Username is not correctly formatted"));
    }

    @Test
    public void testRegisterUserFailedPassword() {
        String result = login.registerUser("kyl_1", "password", "+27831234567");
        assertTrue(result.contains("Password is not correctly formatted"));
    }

    @Test
    public void testLoginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27831234567");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27831234567");
        assertFalse(login.loginUser("kyl_1", "wrongPass1!"));
    }
}