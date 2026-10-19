# ChatApp - PROG5121 POE Part 2

## QuickChat: Registration, Login & Messaging
This is a console-based Java application. Users register and log in (Part 1), then send, discard or store messages (Part 2). It validates the username, password complexity, South African cellphone numbers and message length.

## Project Structure
- `ChatApp.java` - Main class: registration, login, welcome message, menu loop and message entry
- `Login.java` - Handles all validation logic and authentication
- `Message.java` - Creates message IDs and hashes, validates recipients and length, sends/discards/stores messages
- `LoginTest.java` - JUnit tests for the registration and login methods
- `MessageTest.java` - JUnit tests for the message methods
- `.github/workflows/maven.yml` - GitHub Actions workflow that runs the tests on every push

## Validation Rules Implemented

**Username:** Must contain an underscore `_` and be 5 characters or less.

**Password:** Must be at least 8 characters, contain a capital letter, a number, and a special character.

**Cellphone / Recipient:** Must be a South African number in the format `+27` followed by no more than ten digits. Validated using Regex.
Reference: https://stackoverflow.com/questions/19322685/simple-regex-for-south-african-phone-number

**Message:** Must not exceed 250 characters.

## Features
- `checkUserName()` - Validates username format
- `checkPasswordComplexity()` - Validates password strength
- `checkCellPhoneNumber()` - Validates SA cellphone format
- `registerUser()` - Returns registration success/fail messages
- `loginUser()` - Authenticates user credentials
- `returnLoginStatus()` - Returns welcome message if login succeeds
- `checkMessageID()` - Checks the message ID is no more than ten characters
- `checkRecipientCell()` - Validates the recipient's cell number
- `createMessageHash()` - Builds the hash, e.g. `00:0:HITONIGHT`
- `sentMessage()` - Sends, disregards or stores a message
- `printMessages()` - Returns all messages sent while the program runs
- `returnTotalMessages()` - Returns the total number of messages sent
- `storeMessage()` - Stores messages in a JSON file (`storedMessages.json`)

## Menu
1. Send Messages
2. Show recently sent messages (Coming Soon)
3. Quit

## Tests
Unit tests are written in `LoginTest.java` and `MessageTest.java` using JUnit 5. They use the test data from the brief.

Tests cover:
- Username, password and cellphone correctly and incorrectly formatted (messages and true/false results)
- Registration success and failure
- Login success and failure
- Message length success and failure
- Recipient number correctly and incorrectly formatted
- Message hash (including a loop over both test messages)
- Message ID creation
- Send, disregard and store options
- Total messages sent and printed messages

Tests are also run automatically with GitHub Actions on every push.

## How to Run
1. Open project in NetBeans
2. Right-click the project > Test to run all tests
3. Run `ChatApp.java` to start the application

## Technologies
- Java JDK 25
- NetBeans 25
- JUnit 5.10.3
- Maven
- GitHub Actions
