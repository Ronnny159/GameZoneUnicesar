package com.gameZone.ui;

import com.gameZone.model.Return;
import com.gameZone.service.ReturnService;
import com.gameZone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReturnMenu {

    private final ReturnService returnService;
    private final SaleService saleService;
    private final Scanner input;

    public ReturnMenu(ReturnService returnService, SaleService saleService, Scanner input) {
        this.returnService = returnService;
        this.saleService = saleService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- RETURN MANAGEMENT ---");
            System.out.println("  1. Register Return");
            System.out.println("  2. View All Returns");
            System.out.println("  3. View Returns by Customer");
            System.out.println("  4. View Returns by Sale");
            System.out.println("  5. Monthly Balance");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerReturn();
                case 2 -> viewAllReturns();
                case 3 -> viewReturnsByCustomer();
                case 4 -> viewReturnsBySale();
                case 5 -> showMonthlyBalance();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerReturn() {
        String saleId = ConsoleUtils.readString(input, "Sale ID: ");
        List<String> productIds = new ArrayList<>();
        while (true) {
            String pid = ConsoleUtils.readString(input, "Product ID (or 'done'): ");
            if (pid.equalsIgnoreCase("done")) break;
            productIds.add(pid);
        }
        String reason = ConsoleUtils.readString(input, "Reason: ");
        try {
            Return r = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("\nReturn registered successfully!");
            System.out.println(r.generateReturnReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllReturns() {
        List<Return> returns = returnService.viewAllReturns();
        if (returns.isEmpty()) {
            System.out.println("No returns registered.");
            return;
        }
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
        }
    }

    private void viewReturnsByCustomer() {
        String customerId = ConsoleUtils.readString(input, "Customer ID: ");
        List<Return> returns = returnService.viewReturnsByCustomer(customerId);
        if (returns.isEmpty()) {
            System.out.println("No returns found for this customer.");
            return;
        }
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
        }
    }

    private void viewReturnsBySale() {
        String saleId = ConsoleUtils.readString(input, "Sale ID: ");
        List<Return> returns = returnService.viewReturnsBySale(saleId);
        if (returns.isEmpty()) {
            System.out.println("No returns found for this sale.");
            return;
        }
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
        }
    }

    public void showMonthlyBalance() {
        int month = ConsoleUtils.readInt(input, "Month (1-12): ");
        int year = ConsoleUtils.readInt(input, "Year: ");
        double sales = returnService.calculateMonthlySales(month, year);
        double returns = returnService.calculateMonthlyReturns(month, year);
        double balance = returnService.generateMonthlyBalance(month, year);
        System.out.println("\n--- MONTHLY BALANCE " + month + "/" + year + " ---");
        System.out.println("  Total sales:     $" + sales);
        System.out.println("  Total returns:   $" + returns);
        System.out.println("  Net balance:     $" + balance);
    }
}