package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionService {

    private PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public PercentageDiscount registerPercentageDiscount(String id, String name,
                                                          LocalDate startDate, LocalDate endDate,
                                                          double percentage) {
        PercentageDiscount promotion = new PercentageDiscount(id, name, startDate, endDate, percentage);
        persist(promotion);
        return promotion;
    }

    public CategoryDiscount registerCategoryDiscount(String id, String name,
                                                      LocalDate startDate, LocalDate endDate,
                                                      String category, double percentage) {
        if (!category.equalsIgnoreCase("VIDEOGAME") &&
            !category.equalsIgnoreCase("CONSOLE") &&
            !category.equalsIgnoreCase("ACCESSORY")) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
        CategoryDiscount promotion = new CategoryDiscount(id, name, startDate, endDate, category, percentage);
        persist(promotion);
        return promotion;
    }

    public BulkPurchaseDiscount registerBulkPurchaseDiscount(String id, String name,
                                                              LocalDate startDate, LocalDate endDate,
                                                              int minQuantity, double percentage) {
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
        persist(promotion);
        return promotion;
    }

    public List<Promotion> listAllPromotions() {
        return promotionRepository.loadAll();
    }

    public List<Promotion> listActivePromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> active = new ArrayList<>();
        for (Promotion p : promotionRepository.loadAll()) {
            if (p.isActive(today)) {
                active.add(p);
            }
        }
        return active;
    }

    public Promotion findById(String promotionId) {
        for (Promotion p : promotionRepository.loadAll()) {
            if (p.getId().equals(promotionId)) {
                return p;
            }
        }
        return null;
    }

    public Promotion findBestPromotionFor(Sale sale) {
        List<Promotion> promotions = promotionRepository.loadAll();
        Promotion bestPromotion = null;
        double maxDiscount = 0;

        for (Promotion promotion : promotions) {
            if (promotion.isActive(LocalDate.now())) {
                double discount = promotion.calculateDiscount(sale);
                if (discount > maxDiscount) {
                    maxDiscount = discount;
                    bestPromotion = promotion;
                }
            }
        }

        return bestPromotion;
    }

    private void persist(Promotion promotion) {
        List<Promotion> all = promotionRepository.loadAll();
        all.add(promotion);
        promotionRepository.saveAll(all);
    }
}