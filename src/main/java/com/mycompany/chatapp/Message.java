package com.mycompany.chatapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single chat message and keeps track of the messages
 * sent and stored while the program is running.
 */
public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final Random random = new Random();
    private static Path storageFile = Paths.get("storedMessages.json");

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;
    private final String messageHash;

    /** Creates a message with an auto-generated ten-digit message ID. */
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(), messageNumber, recipient, text);
    }

    /** Creates a message with a given ID (used by the unit tests). */
    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    /** Builds a random ten-digit ID one digit at a time. */
    private static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /** Checks that the message ID is not more than ten characters long. */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    /** Returns the text confirming the generated message ID. */
    public String getMessageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    /** Checks a recipient number using the Part 1 cell phone rule. */
    public static boolean isValidRecipient(String recipient) {
        return recipient != null && new Login().checkCellPhoneNumber(recipient);
    }

    /** Returns the success or failure message for this message's recipient. */
    public String checkRecipientCell() {
        if (isValidRecipient(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /** Checks that a message is not longer than 250 characters. */
    public static boolean isValidLength(String text) {
        return text != null && text.length() <= MAX_MESSAGE_LENGTH;
    }

    /** Returns the success or failure message for a message's length. */
    public static String checkMessageLength(String text) {
        if (isValidLength(text)) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /**
     * Creates the hash: first two digits of the ID, the message number,
     * then the first and last words of the message, all in capitals.
     * Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String[] words = text.trim().split("\\s+");
        String firstWord = cleanWord(words[0]);
        String lastWord = cleanWord(words[words.length - 1]);
        String idStart = messageID.substring(0, Math.min(2, messageID.length()));
        return (idStart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    /** Removes punctuation such as commas and question marks from a word. */
    private static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Handles the user's choice: 1 = send, 2 = disregard, 3 = store.
     * Returns the message to show the user.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case SEND:
                sentMessages.add(this);
                return "Message successfully sent.";
            case DISREGARD:
                return "Press 0 to delete the message.";
            case STORE:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option; please choose 1 (send), 2 (disregard) or 3 (store).";
        }
    }

    /** Returns the full details of every message sent so far. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder output = new StringBuilder();
        for (Message message : sentMessages) {
            output.append(message.getDetails()).append("\n\n");
        }
        return output.toString().trim();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    /**
     * Stores this message in a JSON file. The file holds every message
     * stored during this run, written as a JSON array.
     */
    public boolean storeMessage() {
        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
        }
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            json.append(storedMessages.get(i).toJson());
            json.append(i < storedMessages.size() - 1 ? ",\n" : "\n");
        }
        json.append("]\n");
        try {
            Files.writeString(storageFile, json.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String toJson() {
        return "  {\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(text) + "\"\n"
                + "  }";
    }

    /** Escapes characters that are not allowed raw inside a JSON string. */
    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }

    /** Message ID, hash, recipient and message, in that order. */
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    /** Clears the sent and stored lists (used by the unit tests). */
    public static void resetMessages() {
        sentMessages.clear();
        storedMessages.clear();
    }

    /** Changes where stored messages are saved (used by the unit tests). */
    public static void setStorageFile(Path file) {
        storageFile = file;
    }
}
