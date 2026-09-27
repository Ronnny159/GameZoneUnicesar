package com.gameZone.model;

import java.time.LocalDate;

public abstract class Warranty {
    protected String id;
    protected Product product;
    protected Sale sale;
    protected LocalDate startDate;
    protected LocalDate endDate;

    public Warranty(String id, Product product, Sale sale, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }
    public Product getProduct() {
        return product;
    }
    public Sale getSale() {
        return sale;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }

    public abstract int getDurationInMonths();
    public abstract String getWarrantyType();
    public abstract double getAdditionalcost();

    public boolean isActive(LocalDate date) {
        LocalDate today = LocalDate.now();
        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
               (today.isEqual(endDate) || today.isBefore(endDate));
    }

    public String generateWarrantyCertificate() {
        return "Warranty Certificate\n" +
               "-------------------\n" +
               "Warranty ID: " + id + "\n" +
               "Product: " + product.getTitle() + "\n" +
               "Sale ID: " + sale.getid() + "\n" +
               "Start Date: " + startDate + "\n" +
               "End Date: " + endDate + "\n" +
               "Duration: " + getDurationInMonths() + " months\n" +
               "Warranty Type: " + getWarrantyType() + "\n" +
               "Additional Cost: $" + getAdditionalcost();
    }
}