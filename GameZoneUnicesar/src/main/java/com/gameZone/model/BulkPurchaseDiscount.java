package com.gameZone.model;

import java.time.LocalDate;

public class BulkPurchaseDiscount extends Promotion {

    private int minQuantity;
    private double percentage;

    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minQuantity, double percentage) {
        super(id, name, startDate, endDate);
        this.minQuantity = minQuantity;
        this.percentage = percentage;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        if (sale.getproducts().size() >= minQuantity) {
            return sale.calculateprice()* (percentage / 100);
        }
        return 0.0;
    }
    
}
