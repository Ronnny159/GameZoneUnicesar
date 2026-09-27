package com.gameZone.ui;

import com.gameZone.model.*;
import com.gameZone.service.AccessoryService;
import com.gameZone.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccessoryMenu {

    private final AccessoryService accessoryService;
    private final ProductService productService;
    private final Scanner input;

    public AccessoryMenu(AccessoryService accessoryService, ProductService productService, Scanner input) {
        this.accessoryService = accessoryService;
        this.productService = productService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- ACCESSORY MANAGEMENT ---");
            System.out.println("  1. Register Cable");
            System.out.println("  2. Register Controller");
            System.out.println("  3. Register Memory");
            System.out.println("  4. List Accessories");
            System.out.println("  5. List Accessories By Type");
            System.out.println("  6. Find Accessories Compatible with Console");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerCable();
                case 2 -> registerController();
                case 3 -> registerMemory();
                case 4 -> listAccessories();
                case 5 -> listByType();
                case 6 -> findCompatible();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerCable() {
        System.out.println("\n--- REGISTER CABLE ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String title = ConsoleUtils.readString(input, "Title: ");
        double price = ConsoleUtils.readDouble(input, "Price: ");
        int quantity = ConsoleUtils.readInt(input, "Quantity: ");
        double length = ConsoleUtils.readDouble(input, "Length: ");
        String connector = ConsoleUtils.readString(input, "Connector Type: ");
        Cable cable = accessoryService.registerCable(id, title, price, quantity, length, connector);
        System.out.println("Cable registered: " + cable.getTitle());
    }

    private void registerController() {
        System.out.println("\n--- REGISTER CONTROLLER ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String title = ConsoleUtils.readString(input, "Title: ");
        double price = ConsoleUtils.readDouble(input, "Price: ");
        int quantity = ConsoleUtils.readInt(input, "Quantity: ");
        String connection = ConsoleUtils.readString(input, "Connection Type (Wired/Wireless): ");
        List<String> compatibleConsoles = selectCompatibleConsoles();
        Controller controller = accessoryService.registerController(id, title, price, quantity,
                connection, compatibleConsoles);
        System.out.println("Controller registered: " + controller.getTitle());
    }

    private void registerMemory() {
        System.out.println("\n--- REGISTER MEMORY ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String title = ConsoleUtils.readString(input, "Title: ");
        double price = ConsoleUtils.readDouble(input, "Price: ");
        int quantity = ConsoleUtils.readInt(input, "Quantity: ");
        int gigabytes = ConsoleUtils.readInt(input, "Gigabytes: ");
        String memoryType = ConsoleUtils.readString(input, "Memory Type: ");
        List<String> compatibleConsoles = selectCompatibleConsoles();
        Memory memory = accessoryService.registerMemory(id, title, price, quantity,
                gigabytes, memoryType, compatibleConsoles);
        System.out.println("Memory registered: " + memory.getTitle());
    }

    private List<String> selectCompatibleConsoles() {
        List<String> compatible = new ArrayList<>();
        List<Product> products = productService.getAllProducts();
        boolean hasConsoles = false;
        System.out.println("Available consoles:");
        for (Product p : products) {
            if (p instanceof Console) {
                System.out.println("  " + p.getId() + " | " + p.getTitle());
                hasConsoles = true;
            }
        }
        if (!hasConsoles) {
            System.out.println("  No consoles registered yet.");
            return compatible;
        }
        while (true) {
            String consoleId = ConsoleUtils.readString(input, "Console ID (or 'done'): ");
            if (consoleId.equalsIgnoreCase("done")) break;
            Product p = productService.findById(consoleId);
            if (p instanceof Console) {
                compatible.add(consoleId);
                System.out.println("  Added: " + p.getTitle());
            } else {
                System.out.println("  Console not found.");
            }
        }
        return compatible;
    }

    private void listAccessories() {
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No accessories registered.");
            return;
        }
        System.out.println("\n--- ACCESSORIES (" + accessories.size() + ") ---");
        for (Accessory a : accessories) {
            System.out.println("  " + a.getDescription());
        }
    }

    private void listByType() {
        String type = ConsoleUtils.readString(input, "Type (CONTROLLER/CABLE/MEMORY): ");
        List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
        if (accessories.isEmpty()) {
            System.out.println("No accessories found of type: " + type);
            return;
        }
        for (Accessory a : accessories) {
            System.out.println("  " + a.getDescription());
        }
    }

    private void findCompatible() {
        String consoleId = ConsoleUtils.readString(input, "Console ID: ");
        Product console = productService.findById(consoleId);
        if (!(console instanceof Console)) {
            System.out.println("Console not found.");
            return;
        }
        List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
        if (accessories.isEmpty()) {
            System.out.println("No compatible accessories with: " + console.getTitle());
            return;
        }
        System.out.println("--- COMPATIBLE WITH " + console.getTitle() + " ---");
        for (Accessory a : accessories) {
            System.out.println("  " + a.getDescription());
        }
    }
}