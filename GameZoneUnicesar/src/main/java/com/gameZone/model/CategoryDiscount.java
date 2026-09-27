package com.gameZone.model;

import java.time.LocalDate;

public class CategoryDiscount extends Promotion {

    private String category;
    private double discountAmount;

    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String category, double discountAmount) {
        super(id, name, startDate, endDate);
        this.category = category;
        this.discountAmount = discountAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        boolean matchesCategory = false;
        for(Product product : sale.getproducts()) {
            if (category.equalsIgnoreCase("VIDEOGAME")
                && product instanceof VideoGame) {
                matchesCategory = true;
            } else if (category.equalsIgnoreCase("CONSOLE")
                && product instanceof Console) {
                matchesCategory = true;
            } else if (category.equalsIgnoreCase("ACCESSORY")
                && product instanceof Accesory) {
                matchesCategory = true;
            }
            if (matchesCategory) {
            discountAmount += sale.calculateprice();
            }
        }
        return discountAmount * (discountAmount / 100);
    }
    
}
