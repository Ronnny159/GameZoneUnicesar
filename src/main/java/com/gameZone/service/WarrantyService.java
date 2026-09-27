package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarrantyService {

    private WarrantyRepository warrantyRepository;
    private ProductService productService;

    public WarrantyService(WarrantyRepository warrantyRepository, ProductService productService) {
        this.warrantyRepository = warrantyRepository;
        this.productService = productService;
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        if (product == null || sale == null) {
            throw new IllegalArgumentException("Product and Sale cannot be null.");
        }
        String id = "BW-" + sale.getid() + "-" + product.getId();
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);

        List<Warranty> all = warrantyRepository.loadAll();
        all.add(warranty);
        warrantyRepository.saveAll(all);
        return warranty;
    }

    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        if (product == null || sale == null) {
            throw new IllegalArgumentException("Product and Sale cannot be null.");
        }
        String id = "EW-" + sale.getid() + "-" + product.getId();
        ExtendedWarranty warranty = new ExtendedWarranty(id, product, sale, startDate);

        List<Warranty> all = warrantyRepository.loadAll();
        all.add(warranty);
        warrantyRepository.saveAll(all);
        return warranty;
    }

    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty w : warrantyRepository.loadAll()) {
            if (w.getProduct().getId().equals(productId) &&
                w.getSale().getid().equals(saleId)) {
                return w;
            }
        }
        return null;
    }

    public List<Warranty> listAllWarranties() {
        return warrantyRepository.loadAll();
    }

    public List<Warranty> listActiveWarranties() {
        LocalDate today = LocalDate.now();
        List<Warranty> active = new ArrayList<>();
        for (Warranty w : warrantyRepository.loadAll()) {
            if (w.isActive(today)) {
                active.add(w);
            }
        }
        return active;
    }

    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysAhead);
        List<Warranty> expiringSoon = new ArrayList<>();
        for (Warranty w : warrantyRepository.loadAll()) {
            if (!w.getEndDate().isBefore(today) && !w.getEndDate().isAfter(thresholdDate)) {
                expiringSoon.add(w);
            }
        }
        return expiringSoon;
    }

    public double cancelWarranties(String productId, String saleId) {
        List<Warranty> warranties = warrantyRepository.loadAll();
        double refund = 0.0;
        List<Warranty> toRemove = new ArrayList<>();
        for (Warranty w : warranties) {
            if (w.getProduct().getId().equals(productId) &&
                w.getSale().getid().equals(saleId)) {
                refund += w.getAdditionalCost();
                toRemove.add(w);
            }
        }
        warranties.removeAll(toRemove);
        warrantyRepository.saveAll(warranties);
        return refund;
    }
}