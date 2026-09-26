package com.gameZone.model;

import java.time.LocalDate;

public abstract class Promotion {

    protected String id;
    protected String name;
    protected LocalDate startDate;
    protected LocalDate endDate;

    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public boolean isActive(LocalDate date) {
        LocalDate today = LocalDate.now();
        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
               (today.isEqual(endDate) || today.isBefore(endDate));
    }

    public abstract double calculateDiscount(Sale sale);
}
