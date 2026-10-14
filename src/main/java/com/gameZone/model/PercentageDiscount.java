package com.gameZone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a percentage discount to the total sale.
 */
public class PercentageDiscount extends Promotion {

    private double percentage;

    public PercentageDiscount(String id, String name, LocalDate startDate,
                               LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        return sale.calculatePrice() * (percentage / 100);
    }
}