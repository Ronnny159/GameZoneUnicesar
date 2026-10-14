package com.gameZone.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Helper methods for safe console input.
 */
public class ConsoleUtils {

    public static int readInt(Scanner input, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = input.nextInt();
                input.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                input.nextLine();
            }
        }
    }

    public static double readDouble(Scanner input, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = input.nextDouble();
                input.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                input.nextLine();
            }
        }
    }

    public static String readString(Scanner input, String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
}