package com.gameZone.model;

import java.io.Serializable;

public abstract class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private double price;
    private int quantity;

    public Product(String id, String title, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Returns the product category used by category-based promotions.
     *
     * @return the product category
     */
    public abstract Category getCategory();

    /**
     * Returns true if this product requires a basic warranty when sold.
     * By default, products do not require a warranty. Consoles override
     * this method to return true.
     *
     * @return true if the product requires a basic warranty
     */
    public boolean requiresBasicWarranty() {
        return false;
    }

    /**
     * Returns true if this product can have an extended warranty.
     * By default, products cannot. Consoles override this method.
     *
     * @return true if extended warranty is available
     */
    public boolean supportsExtendedWarranty() {
        return false;
    }

    /**
     * Returns true if this product is an accessory.
     * By default, false. Accessory overrides this method.
     *
     * @return true if the product is an accessory
     */
    public boolean isAccessory() {
        return false;
    }

    /**
     * Returns a full description. Must be implemented by subclasses.
     *
     * @return formatted description string
     */
    public abstract String getDescription();
}