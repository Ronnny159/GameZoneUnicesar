package com.gameZone.model;

import java.time.LocalDate;

public class CategoryDiscount extends Promotion {

    private String category;
    private double percentage;

    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String category, double percentage) {
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
            percentage += sale.calculateprice();
            }
        }
        return percentage * (percentage / 100);
    }
    
}
