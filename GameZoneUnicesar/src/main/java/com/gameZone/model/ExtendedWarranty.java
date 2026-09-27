package com.gameZone.model;

import java.time.LocalDate;

public class ExtendedWarranty extends Warranty {

    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() { return 12; }

    @Override
    public String getWarrantyType() { return "Garantía Extendida"; }

    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * 0.10;
    }
}
