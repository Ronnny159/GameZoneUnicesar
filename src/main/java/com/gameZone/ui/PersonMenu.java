package com.gameZone.ui;

import com.gameZone.model.Customer;
import com.gameZone.model.Seller;
import com.gameZone.service.PersonService;

import java.util.List;
import java.util.Scanner;

public class PersonMenu {

    private final PersonService personService;
    private final Scanner input;

    public PersonMenu(PersonService personService, Scanner input) {
        this.personService = personService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- PERSON MANAGEMENT ---");
            System.out.println("  1. Register Customer");
            System.out.println("  2. List Customers");
            System.out.println("  3. List Sellers");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerCustomer();
                case 2 -> listCustomers();
                case 3 -> listSellers();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerCustomer() {
        System.out.println("\n--- REGISTER CUSTOMER ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String name = ConsoleUtils.readString(input, "Name: ");
        String phone = ConsoleUtils.readString(input, "Phone: ");
        String email = ConsoleUtils.readString(input, "Email: ");
        try {
            Customer c = personService.registerCustomer(id, name, phone, email);
            System.out.println("Customer registered: " + c.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCustomers() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        System.out.println("\n--- CUSTOMERS (" + customers.size() + ") ---");
        for (Customer c : customers) {
            System.out.println("  " + c.getRoleDescription());
        }
    }

    private void listSellers() {
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No sellers registered.");
            return;
        }
        System.out.println("\n--- SELLERS (" + sellers.size() + ") ---");
        for (Seller s : sellers) {
            System.out.println("  " + s.getRoleDescription());
        }
    }
}