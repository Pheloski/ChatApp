package com.mycompany.chatapp;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Login login = new Login();

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
        sc.close();
    }
}