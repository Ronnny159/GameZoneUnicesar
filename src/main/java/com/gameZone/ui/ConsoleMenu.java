package com.gameZone.ui;

import com.gameZone.service.*;

import java.util.Scanner;

/**
 * Main console menu that delegates to each module's submenu.
 */
public class ConsoleMenu {

    private final PersonMenu personMenu;
    private final ProductMenu productMenu;
    private final AccessoryMenu accessoryMenu;
    private final PromotionMenu promotionMenu;
    private final SaleMenu saleMenu;
    private final WarrantyMenu warrantyMenu;
    private final ReturnMenu returnMenu;
    private final Scanner input;

    public ConsoleMenu(PersonService personService, ProductService productService,
                       AccessoryService accessoryService, PromotionService promotionService,
                       SaleService saleService, WarrantyService warrantyService,
                       ReturnService returnService, Scanner input) {
        this.input = input;
        this.personMenu = new PersonMenu(personService, input);
        this.productMenu = new ProductMenu(productService, input);
        this.accessoryMenu = new AccessoryMenu(accessoryService, productService, input);
        this.promotionMenu = new PromotionMenu(promotionService, input);
        this.saleMenu = new SaleMenu(saleService, personService, productService,
                accessoryService, warrantyService, input);
        this.warrantyMenu = new WarrantyMenu(warrantyService, input);
        this.returnMenu = new ReturnMenu(returnService, saleService, input);
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║        GAMEZONE UNICESAR             ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.println("--- PERSON MANAGEMENT ---");
            System.out.println("  1. Person Menu");
            System.out.println("--- PRODUCT MANAGEMENT ---");
            System.out.println("  2. Product Menu");
            System.out.println("  3. Accessory Menu");
            System.out.println("--- PROMOTIONS ---");
            System.out.println("  4. Promotion Menu");
            System.out.println("--- SALES ---");
            System.out.println("  5. Sale Menu");
            System.out.println("--- WARRANTIES ---");
            System.out.println("  6. Warranty Menu");
            System.out.println("--- RETURNS ---");
            System.out.println("  7. Return Menu");
            System.out.println("  0. EXIT");

            int option = ConsoleUtils.readInt(input, "\nSELECT AN OPTION: ");
            switch (option) {
                case 1 -> personMenu.show();
                case 2 -> productMenu.show();
                case 3 -> accessoryMenu.show();
                case 4 -> promotionMenu.show();
                case 5 -> saleMenu.show();
                case 6 -> warrantyMenu.show();
                case 7 -> returnMenu.show();
                case 0 -> {
                    System.out.println("¡GRACIAS POR USAR GAMEZONE UNICESAR!");
                    exit = true;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}