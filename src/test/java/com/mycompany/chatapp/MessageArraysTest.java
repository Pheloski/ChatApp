package com.mycompany.chatapp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 tests, using the five test messages from the brief.
 */
public class MessageArraysTest {

    private static final String TEXT_1 = "Did you get the cake?";
    private static final String TEXT_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String TEXT_3 = "Yohoooo, I am at your gate.";
    private static final String TEXT_4 = "It is dinner time!";
    private static final String TEXT_5 = "Ok, I am leaving without you.";

    private static final String HASH_2 = "20:1:WHERETIME";

    @TempDir
    Path tempDir;

    private Path jsonFile;

    @BeforeEach
    public void setUp() {
        Message.resetMessages();
        jsonFile = tempDir.resolve("storedMessages.json");
        Message.setStorageFile(jsonFile);
        loadTestData();
    }

    /** Fills the arrays with test data messages 1 to 5 using their flags. */
    private void loadTestData() {
        Message message1 = new Message("1000000001", 0, "Developer", "+27834557896", TEXT_1);
        Message message2 = new Message("2000000002", 1, "Developer", "+27838884567", TEXT_2);
        Message message3 = new Message("3000000003", 2, "Developer", "+27834484567", TEXT_3);
        Message message4 = new Message("4000000004", 3, "Developer", "0838884567", TEXT_4);
        Message message5 = new Message("5000000005", 4, "Developer", "+27838884567", TEXT_5);

        message1.sentMessage(Message.SEND);
        message2.sentMessage(Message.STORE);
        message3.sentMessage(Message.DISREGARD);
        message4.sentMessage(Message.SEND);
        message5.sentMessage(Message.STORE);
    }

    // ---------- Arrays correctly populated ----------

    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time!"),
                Message.getSentMessageTexts());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        assertEquals(Arrays.asList("Yohoooo, I am at your gate."),
                Message.getDisregardedMessageTexts());
    }

    @Test
    public void testStoredMessagesArrayPopulated() {
        assertEquals(Arrays.asList(TEXT_2, TEXT_5), Message.getStoredMessageTexts());
    }

    @Test
    public void testMessageIDArrayPopulated() {
        assertEquals(Arrays.asList("1000000001", "2000000002", "3000000003",
                "4000000004", "5000000005"), Message.getMessageIDs());
    }

    @Test
    public void testMessageHashArrayPopulated() {
        assertEquals(Arrays.asList("10:0:DIDCAKE", "20:1:WHERETIME", "30:2:YOHOOOOGATE",
                "40:3:ITTIME", "50:4:OKYOU"), Message.getMessageHashes());
    }

    // ---------- Reading the JSON file into the stored array ----------

    @Test
    public void testStoredMessagesReadFromJsonFile() {
        Message.resetMessages();   // empty the arrays, the JSON file stays on disk

        int loaded = Message.loadStoredMessages();

        assertEquals(2, loaded);
        assertEquals(Arrays.asList(TEXT_2, TEXT_5), Message.getStoredMessageTexts());
        assertEquals(Arrays.asList("2000000002", "5000000005"), Message.getMessageIDs());
    }

    @Test
    public void testNoJsonFileLoadsNothing() throws Exception {
        Message.resetMessages();
        Files.delete(jsonFile);

        assertEquals(0, Message.loadStoredMessages());
        assertTrue(Message.getStoredMessageTexts().isEmpty());
    }

    // ---------- Longest message ----------

    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.getLongestStoredMessage());
    }

    // ---------- Search for a message ID ----------

    @Test
    public void testSearchForMessageID() {
        Message found = Message.searchByMessageID("4000000004");

        assertEquals("0838884567", found.getRecipient());
        assertEquals("It is dinner time!", found.getText());
    }

    @Test
    public void testSearchForMessageIDNotFound() {
        assertNull(Message.searchByMessageID("9999999999"));
    }

    // ---------- Search for a recipient ----------

    @Test
    public void testSearchMessagesForRecipient() {
        List<String> found = Message.searchByRecipient("+27838884567");

        assertEquals(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."), found);
    }

    @Test
    public void testSearchMessagesForUnknownRecipient() {
        assertTrue(Message.searchByRecipient("+27000000000").isEmpty());
    }

    // ---------- Delete using the message hash ----------

    @Test
    public void testDeleteMessageUsingHash() {
        String result = Message.deleteMessageByHash(HASH_2);

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\""
                + " successfully deleted.", result);
        assertEquals(Arrays.asList(TEXT_5), Message.getStoredMessageTexts());
        assertFalse(Message.getMessageHashes().contains(HASH_2));
        assertFalse(Message.getMessageIDs().contains("2000000002"));
    }

    @Test
    public void testDeleteUpdatesJsonFile() throws Exception {
        Message.deleteMessageByHash(HASH_2);

        String json = Files.readString(jsonFile);
        assertFalse(json.contains("20:1:WHERETIME"));
        assertTrue(json.contains("50:4:OKYOU"));
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        assertEquals("Message with hash 99:9:NOTHING was not found among the stored messages.",
                Message.deleteMessageByHash("99:9:NOTHING"));
        assertEquals(2, Message.getStoredMessageTexts().size());
    }

    // ---------- Reports ----------

    @Test
    public void testSentMessagesReport() {
        String expected = "Message Hash: 10:0:DIDCAKE\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message Hash: 40:3:ITTIME\n"
                + "Recipient: 0838884567\n"
                + "Message: It is dinner time!";

        assertEquals(expected, Message.getSentMessagesReport());
    }

    @Test
    public void testStoredMessagesReportShowsFullDetails() {
        String report = Message.getStoredMessagesReport();

        assertTrue(report.contains("Message ID: 2000000002"));
        assertTrue(report.contains("Message Hash: 20:1:WHERETIME"));
        assertTrue(report.contains("Sender: Developer"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: " + TEXT_2));
        assertTrue(report.contains("Message ID: 5000000005"));
    }

    @Test
    public void testSenderAndRecipientOfStoredMessages() {
        String expected = "Sender: Developer, Recipient: +27838884567\n\n"
                + "Sender: Developer, Recipient: +27838884567";

        assertEquals(expected, Message.getStoredSendersAndRecipients());
    }
}
