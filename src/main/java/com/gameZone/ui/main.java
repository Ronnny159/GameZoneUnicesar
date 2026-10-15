package com.gameZone.ui;

import com.gameZone.persistence.*;
import com.gameZone.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Persistence
        PersonRepository personRepository = new PersonRepository();
        ProductRepository productRepository = new ProductRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository();
        PromotionRepository promotionRepository = new PromotionRepository();
        SaleRepository saleRepository = new SaleRepository(
                productRepository, accessoryRepository, personRepository);
        WarrantyRepository warrantyRepository = new WarrantyRepository(
                saleRepository, productRepository, accessoryRepository);
        ReturnRepository returnRepository = new ReturnRepository(
                saleRepository, productRepository, accessoryRepository);

        // Services
        PersonService personService = new PersonService(personRepository);
        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        InventoryService inventoryService = new InventoryService(productService, accessoryService);
        PromotionService promotionService = new PromotionService(promotionRepository);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository, productService);
        ReturnService returnService = new ReturnService(
                returnRepository, saleRepository, inventoryService, warrantyService);
        SaleService saleService = new SaleService(
                saleRepository, personService, inventoryService, promotionService, warrantyService);

        // UI
        Scanner input = new Scanner(System.in);
        ConsoleMenu consoleMenu = new ConsoleMenu(personService, productService, accessoryService,
                promotionService, saleService, warrantyService, returnService, input);
        consoleMenu.show();

        input.close();
    }
}