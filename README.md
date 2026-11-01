# ChatApp - PROG5121 POE Part 3
 
## QuickChat: Registration, Login, Messaging & Stored Messages
This is a console-based Java application. Users register and log in, then send, discard or store messages. Stored messages are saved to a JSON file, read back into arrays when the app starts, and can be searched, reported on and deleted.
 
## Project Structure
- `ChatApp.java` - Main class: registration, login, main menu, message entry and the Stored Messages menu
- `Login.java` - Handles all validation logic and authentication
- `Message.java` - Message details, the message arrays, JSON reading and writing, searching, deleting and reports
- `LoginTest.java` - JUnit tests for registration and login
- `MessageTest.java` - JUnit tests for message creation, hashes, IDs and sending
- `MessageArraysTest.java` - JUnit tests for the arrays, JSON file, search, delete and reports
- `.github/workflows/maven.yml` - GitHub Actions workflow that runs all the tests on every push
## Validation Rules Implemented
 
**Username:** Must contain an underscore `_` and be 5 characters or less.
 
**Password:** Must be at least 8 characters, contain a capital letter, a number, and a special character.
 
**Cellphone / Recipient:** Must be a South African number in the format `+27` followed by no more than ten digits. Validated using Regex.
Reference: https://stackoverflow.com/questions/19322685/simple-regex-for-south-african-phone-number
 
**Message:** Must not exceed 250 characters.
 
## Arrays
The arrays are filled by the user while the app runs (nothing is hard-coded):
- Sent messages
- Disregarded messages
- Stored messages (read from `storedMessages.json`)
- Message hashes
- Message IDs
The message ID, message hash and message arrays are parallel arrays: the same index describes the same message.
 
## Menu
1. Send Messages
2. Show recently sent messages (report of hash, recipient and message)
3. Quit
4. Stored Messages
   1. Display the sender and recipient of all stored messages
   2. Display the longest stored message
   3. Search for a message ID and display the recipient and message
   4. Search for all messages for a recipient
   5. Delete a message using its hash
   6. Display a report of all stored messages
   7. Back to the main menu
## JSON
Stored messages are saved to `storedMessages.json`. The file is read back into the stored messages array each time the app starts, and it is updated when a message is deleted.
The Gson library is used to read and write the JSON: Google (n.d.) Gson. Available at: https://github.com/google/gson
 
## Changes from Part 1 and Part 2
 
### Part 1 (after feedback)
- **Cell phone validation:** the regex now accepts `+27` followed by no more than ten digits, as the brief says. Before, it required exactly 12 characters in total.
- **Attribution:** the regex source is now credited in a comment in `Login.java`, as well as in this README.
- **Messages:** the registration messages now match the wording in the brief exactly.
- **Unit tests:** `LoginTest.java` now uses the exact test data from the brief and checks the exact messages with `assertEquals`, instead of only checking that a message contains some text.
### Part 2 (after feedback)
- **Message ID:** the ID is now created with string manipulation and the loop counter. It takes the last 8 digits of a random number with `substring` and adds the message number from the `for` loop as the last 2 digits. Before, it was ten random digits with no link to the loop counter.
- **Recently sent messages:** menu option 2 now displays the messages sent while the program is running (hash, recipient and message). Before, it only said "Coming Soon."
### Part 3 (new)
- Arrays for sent, disregarded and stored messages, message hashes and message IDs.
- Stored messages are read from the JSON file into an array when the app starts.
- A fourth menu option, Stored Messages, with a longest message search, ID and recipient searches, delete by hash and reports.
## Tests
Unit tests are written in `LoginTest.java`, `MessageTest.java` and `MessageArraysTest.java` using JUnit 5. They use the test data from the briefs, including the five Part 3 test messages.
 
Tests cover:
- Username, password and cellphone validation
- Registration and login
- Message length, recipient, hash and ID
- Send, disregard and store options
- The sent, disregarded, stored, hash and ID arrays being populated
- Reading the JSON file into the stored array
- The longest message, message ID search, recipient search and delete using a hash
- The sent and stored message reports
Tests are also run automatically with GitHub Actions on every push.
 
## How to Run
1. Open project in NetBeans
2. Right-click the project > Test to run all tests
3. Run `ChatApp.java` to start the application
## Technologies
- Java JDK 25
- NetBeans 25
- JUnit 5.10.3
- Gson 2.10.1
- Maven
- GitHub Actions