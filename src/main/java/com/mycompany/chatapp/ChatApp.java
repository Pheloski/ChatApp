package com.mycompany.chatapp;

import java.util.List;
import java.util.Scanner;

public class ChatApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Login login = new Login();

        // ---- Part 1: registration and login ----
        System.out.print("Enter first name: ");
        String first = sc.nextLine();
        System.out.print("Enter last name: ");
        String last = sc.nextLine();
        System.out.print("Enter username (must contain _ and <=5 chars): ");
        String user = sc.nextLine();
        System.out.print("Enter password (8+ chars, capital, number, special): ");
        String pass = sc.nextLine();
        System.out.print("Enter cell (e.g. +27831234567): ");
        String cell = sc.nextLine();

        System.out.println(login.registerUser(user, pass, cell));

        System.out.print("Login - Enter username: ");
        String loginUser = sc.nextLine();
        System.out.print("Login - Enter password: ");
        String loginPass = sc.nextLine();

        System.out.println(login.returnLoginStatus(loginUser, loginPass, first, last));

        // ---- Messaging is only available after a successful login ----
        if (!login.loginUser(loginUser, loginPass)) {
            sc.close();
            return;
        }

        String sender = first + " " + last;

        System.out.println("\nWelcome to QuickChat.");

        int loaded = Message.loadStoredMessages();
        if (loaded > 0) {
            System.out.println("Loaded " + loaded + " stored message(s) from the JSON file.");
        }

        int messageLimit = readPositiveNumber(sc, "How many messages do you wish to enter? ");

        boolean messagesEntered = false;
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesEntered) {
                        System.out.println("You have already entered all "
                                + messageLimit + " messages.");
                    } else {
                        sendMessages(sc, messageLimit, sender);
                        messagesEntered = true;
                    }
                    break;
                case "2":
                    System.out.println(Message.getSentMessagesReport());
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                case "4":
                    storedMessagesMenu(sc);
                    break;
                default:
                    System.out.println("Invalid option; please enter 1, 2, 3 or 4.");
            }
        }
        sc.close();
    }

    /** Lets the user enter exactly the number of messages they chose. */
    private static void sendMessages(Scanner sc, int messageLimit, String sender) {
        for (int i = 0; i < messageLimit; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + messageLimit + " ---");

            String recipient = readRecipient(sc);
            String text = readMessageText(sc);

            Message message = new Message(i, sender, recipient, text);
            System.out.println(message.getMessageIDStatus());
            System.out.println("Message Hash: " + message.getMessageHash());

            handleMessageChoice(sc, message);
        }

        System.out.println("\nMessages sent:\n" + Message.printMessages());
        System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
    }

    /** The "Stored Messages" menu, which works on the stored message arrays. */
    private static void storedMessagesMenu(Scanner sc) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to the main menu");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(Message.getStoredSendersAndRecipients());
                    break;
                case "2":
                    System.out.println(Message.getLongestStoredMessage());
                    break;
                case "3":
                    searchByID(sc);
                    break;
                case "4":
                    searchByRecipient(sc);
                    break;
                case "5":
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteMessageByHash(sc.nextLine().trim()));
                    break;
                case "6":
                    System.out.println(Message.getStoredMessagesReport());
                    break;
                case "7":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid option; please enter a number from 1 to 7.");
            }
        }
    }

    private static void searchByID(Scanner sc) {
        System.out.print("Enter the message ID: ");
        Message found = Message.searchByMessageID(sc.nextLine().trim());
        if (found == null) {
            System.out.println("No message was found with that ID.");
        } else {
            System.out.println("Recipient: " + found.getRecipient());
            System.out.println("Message: " + found.getText());
        }
    }

    private static void searchByRecipient(Scanner sc) {
        System.out.print("Enter the recipient's cell number: ");
        List<String> found = Message.searchByRecipient(sc.nextLine().trim());
        if (found.isEmpty()) {
            System.out.println("No messages were found for that recipient.");
        } else {
            for (String text : found) {
                System.out.println("Message: " + text);
            }
        }
    }

    private static String readRecipient(Scanner sc) {
        while (true) {
            System.out.print("Enter recipient cell number (e.g. +27718693002): ");
            String recipient = sc.nextLine().trim();
            if (Message.isValidRecipient(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                return recipient;
            }
            System.out.println("Cell phone number is incorrectly formatted or does not "
                    + "contain an international code. Please correct the number and try again.");
        }
    }

    private static String readMessageText(Scanner sc) {
        while (true) {
            System.out.print("Enter your message (max 250 characters): ");
            String text = sc.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("A message cannot be empty.");
            } else if (Message.isValidLength(text)) {
                System.out.println(Message.checkMessageLength(text));
                return text;
            } else {
                System.out.println(Message.checkMessageLength(text));
            }
        }
    }

    private static void handleMessageChoice(Scanner sc, Message message) {
        int choice = 0;
        while (choice < Message.SEND || choice > Message.STORE) {
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choose an option: ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = 0;
            }
            if (choice < Message.SEND || choice > Message.STORE) {
                System.out.println("Invalid option; please enter 1, 2 or 3.");
            }
        }

        System.out.println(message.sentMessage(choice));

        if (choice == Message.SEND) {
            System.out.println(message.getDetails());
        } else if (choice == Message.DISREGARD) {
            if (sc.nextLine().trim().equals("0")) {
                System.out.println("Message deleted.");
            } else {
                System.out.println("Message disregarded.");
            }
        }
    }

    /** Keeps asking until the user enters a whole number above zero. */
    private static int readPositiveNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(sc.nextLine().trim());
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}
