package com.gameZone.ui;

import com.gameZone.model.Promotion;
import com.gameZone.service.PromotionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PromotionMenu {

    private final PromotionService promotionService;
    private final Scanner input;

    public PromotionMenu(PromotionService promotionService, Scanner input) {
        this.promotionService = promotionService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- PROMOTION MANAGEMENT ---");
            System.out.println("  1. Register Percentage Discount");
            System.out.println("  2. Register Category Discount");
            System.out.println("  3. Register Bulk Purchase Discount");
            System.out.println("  4. List All Promotions");
            System.out.println("  5. List Active Promotions");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerPercentage();
                case 2 -> registerCategory();
                case 3 -> registerBulk();
                case 4 -> listAll();
                case 5 -> listActive();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerPercentage() {
        String id = ConsoleUtils.readString(input, "ID: ");
        String name = ConsoleUtils.readString(input, "Name: ");
        LocalDate start = LocalDate.parse(ConsoleUtils.readString(input, "Start Date (YYYY-MM-DD): "));
        LocalDate end = LocalDate.parse(ConsoleUtils.readString(input, "End Date (YYYY-MM-DD): "));
        double percentage = ConsoleUtils.readDouble(input, "Percentage: ");
        promotionService.registerPercentageDiscount(id, name, start, end, percentage);
        System.out.println("Percentage promotion registered.");
    }

    private void registerCategory() {
        String id = ConsoleUtils.readString(input, "ID: ");
        String name = ConsoleUtils.readString(input, "Name: ");
        LocalDate start = LocalDate.parse(ConsoleUtils.readString(input, "Start Date (YYYY-MM-DD): "));
        LocalDate end = LocalDate.parse(ConsoleUtils.readString(input, "End Date (YYYY-MM-DD): "));
        String category = ConsoleUtils.readString(input, "Category (VIDEOGAME/CONSOLE/ACCESSORY): ");
        double percentage = ConsoleUtils.readDouble(input, "Percentage: ");
        promotionService.registerCategoryDiscount(id, name, start, end, category, percentage);
        System.out.println("Category promotion registered.");
    }

    private void registerBulk() {
        String id = ConsoleUtils.readString(input, "ID: ");
        String name = ConsoleUtils.readString(input, "Name: ");
        LocalDate start = LocalDate.parse(ConsoleUtils.readString(input, "Start Date (YYYY-MM-DD): "));
        LocalDate end = LocalDate.parse(ConsoleUtils.readString(input, "End Date (YYYY-MM-DD): "));
        int minQuantity = ConsoleUtils.readInt(input, "Minimum Quantity: ");
        double percentage = ConsoleUtils.readDouble(input, "Percentage: ");
        promotionService.registerBulkPurchaseDiscount(id, name, start, end, minQuantity, percentage);
        System.out.println("Bulk purchase promotion registered.");
    }

    private void listAll() {
        List<Promotion> promotions = promotionService.listAllPromotions();
        if (promotions.isEmpty()) {
            System.out.println("No promotions registered.");
            return;
        }
        System.out.println("\n--- ALL PROMOTIONS ---");
        for (Promotion p : promotions) {
            System.out.println("  " + p.getId() + " | " + p.getName() +
                    " | " + p.getStartDate() + " to " + p.getEndDate());
        }
    }

    private void listActive() {
        List<Promotion> promotions = promotionService.listActivePromotions();
        if (promotions.isEmpty()) {
            System.out.println("No active promotions.");
            return;
        }
        System.out.println("\n--- ACTIVE PROMOTIONS ---");
        for (Promotion p : promotions) {
            System.out.println("  " + p.getId() + " | " + p.getName());
        }
    }
}