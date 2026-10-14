package com.gameZone.ui;

import com.gameZone.persistence.*;
import com.gameZone.service.*;

import java.util.Scanner;

/**
 * Entry point for the GameZone Unicesar system.
 */
public class Main {

    public static void main(String[] args) {
        // ---------- Persistence layer ----------
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

        // ---------- Service layer ----------
        PersonService personService = new PersonService(personRepository);
        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository, productService);
        ReturnService returnService = new ReturnService(returnRepository, saleRepository,
                productService, accessoryService, warrantyService);
        SaleService saleService = new SaleService(saleRepository, productService, personService,
                accessoryService, promotionService, warrantyService);

        // ---------- UI layer ----------
        Scanner input = new Scanner(System.in);
        ConsoleMenu consoleMenu = new ConsoleMenu(personService, productService, accessoryService,
                promotionService, saleService, warrantyService, returnService, input);
        consoleMenu.show();

        input.close();
    }
}