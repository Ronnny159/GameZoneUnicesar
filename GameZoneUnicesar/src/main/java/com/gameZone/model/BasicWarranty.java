package com.gameZone.model;

import java.time.LocalDate;

public class BasicWarranty extends Warranty {


    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate, LocalDate endDate) {
        super(id, product, sale, startDate, endDate);
    }

    @Override
    public int getDurationInMonths() {
        return 6;
    }

    @Override
    public String getWarrantyType() {
        return "Garantía Básica";
    }

    @Override
    public double getAdditionalcost() {
        return 0.0;
    }
}

