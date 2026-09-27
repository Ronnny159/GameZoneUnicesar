package com.gameZone.ui;

import com.gameZone.model.*;
import com.gameZone.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SaleMenu {

    private final SaleService saleService;
    private final PersonService personService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final WarrantyService warrantyService;
    private final Scanner input;

    public SaleMenu(SaleService saleService, PersonService personService,
                    ProductService productService, AccessoryService accessoryService,
                    WarrantyService warrantyService, Scanner input) {
        this.saleService = saleService;
        this.personService = personService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.warrantyService = warrantyService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- SALE MANAGEMENT ---");
            System.out.println("  1. Register Sale");
            System.out.println("  2. View All Sales");
            System.out.println("  3. View Customer History");
            System.out.println("  4. View Seller History");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerSale();
                case 2 -> viewAllSales();
                case 3 -> viewCustomerHistory();
                case 4 -> viewSellerHistory();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerSale() {
        System.out.println("\n--- REGISTER SALE ---");
        String customerId = ConsoleUtils.readString(input, "Customer ID: ");
        Customer customer = personService.findCustomerById(customerId).orElse(null);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        String sellerId = ConsoleUtils.readString(input, "Seller ID: ");
        Seller seller = personService.findSellerById(sellerId).orElse(null);
        if (seller == null) {
            System.out.println("Seller not found.");
            return;
        }

        List<Product> selected = new ArrayList<>();
        while (true) {
            String productId = ConsoleUtils.readString(input, "Product/Accessory ID (or 'done'): ");
            if (productId.equalsIgnoreCase("done")) break;

            Product p = productService.findById(productId);
            if (p != null) {
                selected.add(p);
                System.out.println("  Added: " + p.getTitle());
                continue;
            }
            try {
                Accessory a = accessoryService.findById(productId);
                selected.add(a);
                System.out.println("  Added accessory: " + a.getTitle());
            } catch (Exception e) {
                System.out.println("  Item not found.");
            }
        }

        if (selected.isEmpty()) {
            System.out.println("A sale must have at least one item.");
            return;
        }

        // Ask for extended warranty on each console
        List<String> extendedWarrantyIds = new ArrayList<>();
        for (Product p : selected) {
            if (p instanceof Console) {
                String answer = ConsoleUtils.readString(input,
                        "Add extended warranty for console '" + p.getTitle() + "'? (y/n): ");
                if (answer.equalsIgnoreCase("y")) {
                    extendedWarrantyIds.add(p.getId());
                }
            }
        }

        try {
            Sale sale = saleService.registerSale(customer, seller, selected, extendedWarrantyIds);
            System.out.println("\nSale registered successfully!");
            System.out.println(sale.generateReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllSales() {
        List<Sale> sales = saleService.getAllSales();
        if (sales.isEmpty()) {
            System.out.println("No sales registered.");
            return;
        }
        for (Sale s : sales) {
            System.out.println("\n" + s.generateReceipt());
        }
        System.out.println("Total revenue: $" + saleService.getTotalRevenue());
    }

    private void viewCustomerHistory() {
        String customerId = ConsoleUtils.readString(input, "Customer ID: ");
        List<Sale> sales = saleService.getCustomerHistory(customerId);
        if (sales.isEmpty()) {
            System.out.println("No purchases found.");
            return;
        }
        for (Sale s : sales) {
            System.out.println("  " + s.getdate() + " | Total: $" + s.calculateTotal());
        }
    }

    private void viewSellerHistory() {
        String sellerId = ConsoleUtils.readString(input, "Seller ID: ");
        List<Sale> sales = saleService.getSellerHistory(sellerId);
        if (sales.isEmpty()) {
            System.out.println("No sales found.");
            return;
        }
        for (Sale s : sales) {
            System.out.println("  " + s.getdate() + " | Customer: " +
                    s.getcostumer().getName() + " | Total: $" + s.calculateTotal());
        }
    }
}