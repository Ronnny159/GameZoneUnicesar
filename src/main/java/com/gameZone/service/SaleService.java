package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleService {

    private SaleRepository saleRepository;
    private PersonService personService;
    private InventoryService inventoryService;
    private PromotionService promotionService;
    private WarrantyService warrantyService;

    public SaleService(SaleRepository saleRepository,
                       PersonService personService,
                       InventoryService inventoryService,
                       PromotionService promotionService,
                       WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.personService = personService;
        this.inventoryService = inventoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a sale applying the best promotion, generating basic
     * warranties for consoles and extended warranties when requested.
     * Now uses polymorphic methods instead of instanceof.
     */
    public Sale registerSale(Customer customer, Seller seller, List<Product> products,
                             List<String> productIdsWithExtendedWarranty) {

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must have at least one product.");
        }

        // 1. Validate stock via InventoryService (V-04, V-07)
        for (Product p : products) {
            if (!inventoryService.hasEnoughStock(p.getId(), 1)) {
                throw new IllegalArgumentException("Insufficient stock for: " + p.getTitle());
            }
        }

        // 2. Create sale
        String saleId = "S-" + System.currentTimeMillis();
        Sale sale = new Sale(saleId, customer, seller, products);

        // 3. Best promotion
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            sale.setAppliedPromotionName(bestPromotion.getName());
            sale.setDiscountAmount(bestPromotion.calculateDiscount(sale));
        }

        // 4. Warranties (V-02: polymorphic)
        List<String> extendedIds = (productIdsWithExtendedWarranty == null)
                ? new ArrayList<>()
                : productIdsWithExtendedWarranty;

        for (Product p : products) {
            if (p.requiresBasicWarranty()) {
                warrantyService.assignBasicWarranty(p, sale, LocalDate.now());

                if (extendedIds.contains(p.getId())) {
                    warrantyService.assignExtendedWarranty(p, sale, LocalDate.now());
                }
            }
        }

        // 5. V-05: use Sale to calculate warranty cost
        double warrantyCost = 0.0;
        for (Warranty w : warrantyService.listAllWarranties()) {
            if (w.getSale().getId().equals(sale.getId()) && w instanceof ExtendedWarranty) {
                warrantyCost += w.getAdditionalCost();
            }
        }
        sale.setExtendedWarrantyCost(warrantyCost);

        // 6. Update inventory via InventoryService
        for (Product p : products) {
            inventoryService.reduceStock(p.getId(), 1);
        }

        // 7. Persist
        List<Sale> sales = saleRepository.loadAll();
        sales.add(sale);
        saleRepository.saveAll(sales);

        return sale;
    }

    public List<Sale> getAllSales() { return saleRepository.loadAll(); }
    public List<Sale> getCustomerHistory(String customerId) { return saleRepository.findSalesByCustomer(customerId); }
    public List<Sale> getSellerHistory(String sellerId) { return saleRepository.findSalesBySeller(sellerId); }

    public double getTotalRevenue() {
        double total = 0;
        for (Sale sale : saleRepository.loadAll()) {
            total += sale.calculateTotal();
        }
        return total;
    }

    /**
     * Returns the products that support extended warranty from a list.
     * Used by the UI to ask the right questions (V-01).
     *
     * @param products the list of products
     * @return the products that support extended warranty
     */
    public List<Product> getExtendedWarrantyEligible(List<Product> products) {
        List<Product> eligible = new ArrayList<>();
        for (Product p : products) {
            if (p.supportsExtendedWarranty()) {
                eligible.add(p);
            }
        }
        return eligible;
    }
}