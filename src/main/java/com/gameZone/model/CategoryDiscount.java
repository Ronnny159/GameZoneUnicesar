package com.gameZone.model;

import java.time.LocalDate;

/**
 * Represents a category-based discount: applies a percentage discount to the
 * subtotal of products belonging to a specific category
 * (VIDEOGAME, CONSOLE, or ACCESSORY).
 */
public class CategoryDiscount extends Promotion {

    private String category;
    private double percentage;

    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                            String category, double percentage) {
        super(id, name, startDate, endDate);
        this.category = category;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        double categorySubtotal = 0.0;
        for (Product product : sale.getProducts()) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                categorySubtotal += product.getPrice();
            }
        }
        return categorySubtotal * (percentage / 100);
    }
}