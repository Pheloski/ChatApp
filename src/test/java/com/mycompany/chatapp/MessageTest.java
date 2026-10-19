/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.mycompany.chatapp;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // Test data from the brief
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        Message.resetMessages();
        Message.setStorageFile(tempDir.resolve("storedMessages.json"));
    }

    // ---------- Message length ----------

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEXT_1));
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMessage));
    }

    // ---------- Recipient number ----------

    @Test
    public void testRecipientCorrectlyFormatted() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        Message message = new Message("9912345678", 1, RECIPIENT_2, TEXT_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                message.checkRecipientCell());
    }

    // ---------- Message hash ----------

    @Test
    public void testMessageHashTestCase1() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] ids = {"0012345678", "9987654321"};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "99:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], i, recipients[i], texts[i]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    // ---------- Message ID ----------

    @Test
    public void testMessageIDCreated() {
        Message message = new Message(0, RECIPIENT_1, TEXT_1);
        assertEquals("Message ID generated: " + message.getMessageID(),
                message.getMessageIDStatus());
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
        assertTrue(message.getMessageID().matches("[0-9]{10}"));
    }

    @Test
    public void testMessageIDTooLong() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);
        assertFalse(message.checkMessageID());
    }

    // ---------- Send / disregard / store ----------

    @Test
    public void testSendMessage() {
        Message message = new Message(0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", message.sentMessage(Message.SEND));
    }

    @Test
    public void testDisregardMessage() {
        Message message = new Message(1, RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(Message.DISREGARD));
    }

    @Test
    public void testStoreMessage() throws Exception {
        Message message = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", message.sentMessage(Message.STORE));

        String json = Files.readString(tempDir.resolve("storedMessages.json"));
        assertTrue(json.contains("\"messageID\": \"0012345678\""));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
        assertTrue(json.contains("\"recipient\": \"+27718693002\""));
        assertTrue(json.contains("\"message\": \"" + TEXT_1 + "\""));
    }

    // ---------- Total messages and printing ----------

    @Test
    public void testTotalMessagesSentWithTestData() {
        Message first = new Message(0, RECIPIENT_1, TEXT_1);
        Message second = new Message(1, RECIPIENT_2, TEXT_2);

        first.sentMessage(Message.SEND);       // test message 1: send
        second.sentMessage(Message.DISREGARD); // test message 2: discard

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessagesShowsSentMessages() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);
        message.sentMessage(Message.SEND);

        String printed = Message.printMessages();
        assertTrue(printed.contains("Message ID: 0012345678"));
        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(printed.contains("Recipient: +27718693002"));
        assertTrue(printed.contains("Message: " + TEXT_1));
    }

    @Test
    public void testNoMessagesSentYet() {
        assertEquals(0, Message.returnTotalMessages());
        assertEquals("No messages have been sent yet.", Message.printMessages());
    }
}
