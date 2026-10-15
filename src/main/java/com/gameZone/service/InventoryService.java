package com.gameZone.service;

import com.gameZone.model.Product;

/**
 * Pure fabrication that centralizes inventory operations across all product
 * types (regular products and accessories). This reduces coupling in
 * SaleService and ReturnService by hiding the two-repository split behind
 * a single interface.
 */
public class InventoryService {

    private ProductService productService;
    private AccessoryService accessoryService;

    public InventoryService(ProductService productService, AccessoryService accessoryService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Checks if enough stock exists for the given product id.
     *
     * @param productId the product id
     * @param amount    the required amount
     * @return true if stock is sufficient
     */
    public boolean hasEnoughStock(String productId, int amount) {
        if (productService.findById(productId) != null) {
            return productService.hasEnoughStock(productId, amount);
        }
        try {
            return accessoryService.findById(productId).getQuantity() >= amount;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reduces stock for the given product id.
     *
     * @param productId the product id
     * @param amount    the amount to reduce
     * @return true if the operation succeeded
     */
    public boolean reduceStock(String productId, int amount) {
        if (productService.findById(productId) != null) {
            return productService.reduceStock(productId, amount);
        }
        try {
            accessoryService.updateStock(productId, -amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Restores stock for the given product id.
     *
     * @param productId the product id
     * @param amount    the amount to restore
     */
    public void restoreStock(String productId, int amount) {
        if (productService.findById(productId) != null) {
            productService.restoreStock(productId, amount);
        } else {
            accessoryService.restoreStock(productId, amount);
        }
    }
}