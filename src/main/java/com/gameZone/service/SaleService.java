package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleService {

    private SaleRepository saleRepository;
    private ProductService productService;
    private PersonService personService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;
    private WarrantyService warrantyService;

    public SaleService(SaleRepository saleRepository,
                       ProductService productService,
                       PersonService personService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale applying the best active promotion, generating
     * basic warranties for consoles and extended warranties when requested.
     */
    public Sale registerSale(Customer customer, Seller seller, List<Product> products,
                             List<String> productIdsWithExtendedWarranty) {

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must have at least one product.");
        }

        // 1. Validate stock
        for (Product p : products) {
            if (p instanceof Accessory) {
                if (!hasEnoughStockAccessory(p.getId(), 1)) {
                    throw new IllegalArgumentException("Insufficient stock for accessory: " + p.getTitle());
                }
            } else {
                if (!productService.hasEnoughStock(p.getId(), 1)) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + p.getTitle());
                }
            }
        }

        // 2. Create the sale (without promotion yet)
        String saleId = "S-" + System.currentTimeMillis();
        Sale sale = new Sale(saleId, customer, seller, products);

        // 3. Apply best promotion (on subtotal)
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            double discount = bestPromotion.calculateDiscount(sale);
            sale.setappliedPromotionName(bestPromotion.getName());
            sale.setdiscountAmount(discount);
        }

        // 4. Generate warranties for consoles
        double warrantyCost = 0.0;
        List<String> extendedIds = (productIdsWithExtendedWarranty == null)
                ? new ArrayList<>()
                : productIdsWithExtendedWarranty;

        for (Product p : products) {
            if (p instanceof Console) {
                // Basic warranty (automatic)
                warrantyService.assignBasicWarranty(p, sale, LocalDate.now());

                // Extended warranty (optional)
                if (extendedIds.contains(p.getId())) {
                    ExtendedWarranty ew = warrantyService.assignExtendedWarranty(p, sale, LocalDate.now());
                    warrantyCost += ew.getAdditionalCost();
                }
            }
        }
        sale.setExtendedWarrantyCost(warrantyCost);

        // 5. Update inventory
        for (Product p : products) {
            if (p instanceof Accessory) {
                accessoryService.updateStock(p.getId(), -1);
            } else {
                productService.reduceStock(p.getId(), 1);
            }
        }

        // 6. Persist sale
        List<Sale> sales = saleRepository.loadAll();
        sales.add(sale);
        saleRepository.saveAll(sales);

        return sale;
    }

    public List<Sale> getAllSales() {
        return saleRepository.loadAll();
    }

    public List<Sale> getCustomerHistory(String customerId) {
        return saleRepository.findSalesByCustomer(customerId);
    }

    public List<Sale> getSellerHistory(String sellerId) {
        return saleRepository.findSalesBySeller(sellerId);
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Sale sale : saleRepository.loadAll()) {
            total += sale.calculateTotal();
        }
        return total;
    }

    private boolean hasEnoughStockAccessory(String id, int amount) {
        try {
            Accessory accessory = accessoryService.findById(id);
            return accessory.getQuantity() >= amount;
        } catch (Exception e) {
            return false;
        }
    }
}