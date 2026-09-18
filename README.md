# ChatApp - PROG5121 POE Part 1

## Registration & Login Feature
This is a console-based Java application that implements user registration and login. It validates username, password complexity, and South African cellphone number format.

## Project Structure
- `ChatApp.java` - Main class that handles user input and output
- `Login.java` - Handles all validation logic and authentication
- `LoginTest.java` - JUnit tests for the validation methods

## Validation Rules Implemented

**Username:** Must contain an underscore `_` and be 5 characters or less.

**Password:** Must be at least 8 characters, contain a capital letter, a number, and a special character.

**Cellphone:** Must be a South African number in the format `+27...` followed by 9 digits. Validated using Regex.
Reference: https://stackoverflow.com/questions/19322685/simple-regex-for-south-african-phone-number

## Features
- `checkUserName()` - Validates username format
- `checkPasswordComplexity()` - Validates password strength
- `checkCellPhoneNumber()` - Validates SA cellphone format
- `registerUser()` - Returns registration success/fail messages
- `loginUser()` - Authenticates user credentials
- `returnLoginStatus()` - Returns welcome message if login succeeds

## Tests
Unit tests are written in `LoginTest.java` using JUnit 5.

Tests run: 11, Failures: 0, Errors: 0 - BUILD SUCCESS

Tests cover:
- Username correctly and incorrectly formatted
- Password complexity correctly and incorrectly formatted
- Cellphone number correctly and incorrectly formatted
- Registration success and failure
- Login success and failure

## How to Run
1. Open project in NetBeans
2. Right-click `LoginTest.java` > Test File to run tests
3. Run `ChatApp.java` to start the application

## Technologies
- Java JDK 23
- NetBeans 25
- JUnit 5.10.3
- Maven
