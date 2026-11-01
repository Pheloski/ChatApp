package com.mycompany.chatapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Represents a single chat message and keeps track of every message
 * created while the program is running.
 *
 * JSON reading and writing uses the Gson library:
 * Google (n.d.) Gson. Available at: https://github.com/google/gson
 */
public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    private static final String FLAG_SENT = "Sent";
    private static final String FLAG_DISREGARD = "Disregard";
    private static final String FLAG_STORED = "Stored";

    // One array per kind of message, filled by the user while the app runs.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();

    // Parallel arrays: index i of each list describes the same message.
    private static final List<String> messageIDs = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<Message> allMessages = new ArrayList<>();

    private static final Random random = new Random();
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private static Path storageFile = Paths.get("storedMessages.json");

    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    @SerializedName("message")
    private String text;
    private String messageHash;
    private String flag;

    /** Creates a message with an auto-generated ten-digit message ID. */
    public Message(int messageNumber, String sender, String recipient, String text) {
        this(generateMessageID(messageNumber), messageNumber, sender, recipient, text);
    }

    /** Creates a message with a given ID and a default sender. */
    public Message(String messageID, int messageNumber, String recipient, String text) {
        this(messageID, messageNumber, "Developer", recipient, text);
    }

    /** Creates a message with a given ID and sender (used by the unit tests). */
    public Message(String messageID, int messageNumber, String sender,
            String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    /** Creates a message with an auto-generated ID and a default sender. */
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageID(messageNumber), messageNumber, "Developer", recipient, text);
    }

    /**
     * Builds a ten-digit message ID using string manipulation:
     * the last 8 digits of a random number (taken with substring),
     * followed by the message number (the loop counter) as 2 digits.
     */
    private static String generateMessageID(int messageNumber) {
        String randomDigits = String.format("%019d", random.nextLong() & Long.MAX_VALUE);
        String randomPart = randomDigits.substring(randomDigits.length() - 8);
        String counterPart = String.format("%02d", messageNumber % 100);
        return randomPart + counterPart;
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
     * Adds the message to the matching array and returns the message
     * to show the user.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case SEND:
                flag = FLAG_SENT;
                if (!sentMessages.contains(this)) {
                    sentMessages.add(this);
                }
                track(this);
                return "Message successfully sent.";
            case DISREGARD:
                flag = FLAG_DISREGARD;
                if (!disregardedMessages.contains(this)) {
                    disregardedMessages.add(this);
                }
                track(this);
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

    /** Adds a message to the parallel arrays if it is not already in them. */
    private static void track(Message message) {
        if (!allMessages.contains(message)) {
            allMessages.add(message);
            messageIDs.add(message.messageID);
            messageHashes.add(message.messageHash);
        }
    }

    /** Removes a message from the parallel arrays. */
    private static void untrack(Message message) {
        int index = allMessages.indexOf(message);
        if (index != -1) {
            allMessages.remove(index);
            messageIDs.remove(index);
            messageHashes.remove(index);
        }
    }

    /** Stores this message in the stored array and saves it to the JSON file. */
    public boolean storeMessage() {
        flag = FLAG_STORED;
        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
        }
        track(this);
        return saveStoredMessages();
    }

    /** Writes the whole stored array to the JSON file. */
    private static boolean saveStoredMessages() {
        try {
            Files.writeString(storageFile, gson.toJson(storedMessages),
                    StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads the JSON file into the stored array.
     * Returns how many stored messages were loaded.
     */
    public static int loadStoredMessages() {
        if (!Files.exists(storageFile)) {
            return 0;
        }
        try {
            String json = Files.readString(storageFile, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Message>>() { }.getType();
            List<Message> loaded = gson.fromJson(json, listType);

            for (Message old : storedMessages) {
                untrack(old);
            }
            storedMessages.clear();

            if (loaded != null) {
                for (Message message : loaded) {
                    if (message != null && message.messageID != null && message.text != null) {
                        message.fillMissingDetails();
                        storedMessages.add(message);
                        track(message);
                    }
                }
            }
            return storedMessages.size();
        } catch (IOException | JsonParseException e) {
            return 0;
        }
    }

    /** Fills in details that older JSON files do not contain. */
    private void fillMissingDetails() {
        flag = FLAG_STORED;
        if (sender == null) {
            sender = "Unknown";
        }
        if (messageHash == null) {
            messageHash = createMessageHash();
        }
    }

    /** Returns the full details of every message sent so far. */
    public static String printMessages() {
        return buildReport(sentMessages, Message::getDetails,
                "No messages have been sent yet.");
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    /** Report of all sent messages: hash, recipient and message. */
    public static String getSentMessagesReport() {
        return buildReport(sentMessages,
                m -> "Message Hash: " + m.messageHash + "\n"
                        + "Recipient: " + m.recipient + "\n"
                        + "Message: " + m.text,
                "No messages have been sent yet.");
    }

    /** Report with the full details of all stored messages. */
    public static String getStoredMessagesReport() {
        return buildReport(storedMessages,
                m -> "Message ID: " + m.messageID + "\n"
                        + "Message Hash: " + m.messageHash + "\n"
                        + "Sender: " + m.sender + "\n"
                        + "Recipient: " + m.recipient + "\n"
                        + "Message: " + m.text,
                "No stored messages.");
    }

    /** Lists the sender and recipient of every stored message. */
    public static String getStoredSendersAndRecipients() {
        return buildReport(storedMessages,
                m -> "Sender: " + m.sender + ", Recipient: " + m.recipient,
                "No stored messages.");
    }

    /** Joins the formatted messages with a blank line between them. */
    private static String buildReport(List<Message> messages,
            Function<Message, String> format, String emptyText) {
        if (messages.isEmpty()) {
            return emptyText;
        }
        StringBuilder report = new StringBuilder();
        for (Message message : messages) {
            report.append(format.apply(message)).append("\n\n");
        }
        return report.toString().trim();
    }

    /** Returns the longest stored message. */
    public static String getLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message message : storedMessages) {
            if (message.text.length() > longest.text.length()) {
                longest = message;
            }
        }
        return longest.text;
    }

    /** Searches the message ID array. Returns the message, or null if not found. */
    public static Message searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        if (index == -1) {
            return null;
        }
        return allMessages.get(index);
    }

    /** Finds the text of every sent or stored message for a recipient. */
    public static List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (Message message : sentMessages) {
            if (message.recipient.equals(recipient)) {
                found.add(message.text);
            }
        }
        for (Message message : storedMessages) {
            if (message.recipient.equals(recipient)) {
                found.add(message.text);
            }
        }
        return found;
    }

    /** Deletes a stored message using its hash and updates the JSON file. */
    public static String deleteMessageByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message message = storedMessages.get(i);
            if (message.messageHash.equalsIgnoreCase(hash)) {
                storedMessages.remove(i);
                untrack(message);
                saveStoredMessages();
                return "Message: \"" + message.text + "\" successfully deleted.";
            }
        }
        return "Message with hash " + hash + " was not found among the stored messages.";
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

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public String getFlag() {
        return flag;
    }

    /** The texts in the sent array. */
    public static List<String> getSentMessageTexts() {
        return textsOf(sentMessages);
    }

    /** The texts in the disregarded array. */
    public static List<String> getDisregardedMessageTexts() {
        return textsOf(disregardedMessages);
    }

    /** The texts in the stored array. */
    public static List<String> getStoredMessageTexts() {
        return textsOf(storedMessages);
    }

    /** The message ID array. */
    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    /** The message hash array. */
    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    private static List<String> textsOf(List<Message> messages) {
        List<String> texts = new ArrayList<>();
        for (Message message : messages) {
            texts.add(message.text);
        }
        return texts;
    }

    /** Clears every array (used by the unit tests). */
    public static void resetMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageIDs.clear();
        messageHashes.clear();
        allMessages.clear();
    }

    /** Changes where stored messages are saved (used by the unit tests). */
    public static void setStorageFile(Path file) {
        storageFile = file;
    }
}
