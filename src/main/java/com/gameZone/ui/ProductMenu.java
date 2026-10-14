package com.gameZone.ui;

import com.gameZone.model.Console;
import com.gameZone.model.Product;
import com.gameZone.model.VideoGame;
import com.gameZone.service.ProductService;

import java.util.List;
import java.util.Scanner;

public class ProductMenu {

    private final ProductService productService;
    private final Scanner input;

    public ProductMenu(ProductService productService, Scanner input) {
        this.productService = productService;
        this.input = input;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- PRODUCT MANAGEMENT ---");
            System.out.println("  1. Register Video Game");
            System.out.println("  2. Register Console");
            System.out.println("  3. List Products");
            System.out.println("  0. Back");

            int option = ConsoleUtils.readInt(input, "Option: ");
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 0 -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerVideoGame() {
        System.out.println("\n--- REGISTER VIDEO GAME ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String title = ConsoleUtils.readString(input, "Title: ");
        double price = ConsoleUtils.readDouble(input, "Price: ");
        int quantity = ConsoleUtils.readInt(input, "Quantity: ");
        String platform = ConsoleUtils.readString(input, "Platform: ");
        String genre = ConsoleUtils.readString(input, "Genre: ");
        String ageRating = ConsoleUtils.readString(input, "Age Rating: ");

        VideoGame game = new VideoGame(id, title, price, quantity, platform, genre, ageRating);
        if (productService.registerProduct(game)) {
            System.out.println("Video game registered successfully!");
        } else {
            System.out.println("Product with ID " + id + " already exists.");
        }
    }

    private void registerConsole() {
        System.out.println("\n--- REGISTER CONSOLE ---");
        String id = ConsoleUtils.readString(input, "ID: ");
        String title = ConsoleUtils.readString(input, "Title: ");
        double price = ConsoleUtils.readDouble(input, "Price: ");
        int quantity = ConsoleUtils.readInt(input, "Quantity: ");
        String brand = ConsoleUtils.readString(input, "Brand: ");
        String model = ConsoleUtils.readString(input, "Model: ");
        int generation = ConsoleUtils.readInt(input, "Generation: ");

        Console console = new Console(id, title, price, quantity, brand, model, generation);
        if (productService.registerProduct(console)) {
            System.out.println("Console registered successfully!");
        } else {
            System.out.println("Product with ID " + id + " already exists.");
        }
    }

    private void listProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products in inventory.");
            return;
        }
        System.out.println("\n--- PRODUCTS (" + products.size() + ") ---");
        for (Product p : products) {
            System.out.println("  " + p.getDescription());
        }
    }
}