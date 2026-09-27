package com.gameZone.model;

import java.time.LocalDate;

public class ExtenderWarranty extends Warranty {
    
    public ExtenderWarranty(String id, Product product, Sale sale, LocalDate startDate, LocalDate endDate) {
        super(id, product, sale, startDate, endDate);
    }

    @Override
    public int getDurationInMonths() {
        return 12;
    }

    @Override
    public String getWarrantyType() {
        return "Garantía Extendida";
    }

    @Override
    public double getAdditionalcost() {
        return 0.1;
    }
}
