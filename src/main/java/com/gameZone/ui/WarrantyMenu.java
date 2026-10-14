package com.gameZone.ui;

import com.gameZone.model.Warranty;
import com.gameZone.service.WarrantyService;

import java.util.List;
import java.util.Scanner;

public class WarrantyMenu {

    private final WarrantyService warrantyService;
    private final Scanner input;

    public WarrantyMenu(WarrantyService warrantyService, Scanner input) {
        this.warrantyService = warrantyService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- WARRANTY MANAGEMENT ---");
            System.out.println("  1. Find Warranty by Product + Sale");
            System.out.println("  2. List All Warranties");
            System.out.println("  3. List Active Warranties");
            System.out.println("  4. List Warranties Expiring Soon");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> findWarranty();
                case 2 -> listAll();
                case 3 -> listActive();
                case 4 -> listExpiringSoon();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void findWarranty() {
        String productId = ConsoleUtils.readString(input, "Product ID: ");
        String saleId = ConsoleUtils.readString(input, "Sale ID: ");
        Warranty w = warrantyService.findWarrantyByProduct(productId, saleId);
        if (w == null) {
            System.out.println("Warranty not found.");
        } else {
            System.out.println(w.generateWarrantyCertificate());
        }
    }

    private void listAll() {
        List<Warranty> warranties = warrantyService.listAllWarranties();
        if (warranties.isEmpty()) {
            System.out.println("No warranties registered.");
            return;
        }
        for (Warranty w : warranties) {
            System.out.println("  " + w.getId() + " | " + w.getWarrantyType() +
                    " | " + w.getProduct().getTitle() +
                    " | Ends: " + w.getEndDate());
        }
    }

    private void listActive() {
        List<Warranty> active = warrantyService.listActiveWarranties();
        if (active.isEmpty()) {
            System.out.println("No active warranties.");
            return;
        }
        for (Warranty w : active) {
            System.out.println("  " + w.getId() + " | " + w.getWarrantyType() +
                    " | " + w.getProduct().getTitle());
        }
    }

    private void listExpiringSoon() {
        int days = ConsoleUtils.readInt(input, "Days ahead: ");
        List<Warranty> expiring = warrantyService.listWarrantiesExpiringSoon(days);
        if (expiring.isEmpty()) {
            System.out.println("No warranties expiring in the next " + days + " days.");
            return;
        }
        for (Warranty w : expiring) {
            System.out.println("  " + w.getId() + " | " + w.getProduct().getTitle() +
                    " | Ends: " + w.getEndDate());
        }
    }
}