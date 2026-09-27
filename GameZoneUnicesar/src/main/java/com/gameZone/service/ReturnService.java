package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.ReturnRepository;
import com.gameZone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnService {

    private ReturnRepository returnRepository;
    private SaleRepository saleRepository;
    private ProductService productService;
    private AccessoryService accessoryService;
    private WarrantyService warrantyService;

    public ReturnService(ReturnRepository returnRepository,
                         SaleRepository saleRepository,
                         ProductService productService,
                         AccessoryService accessoryService,
                         WarrantyService warrantyService) {
        this.returnRepository = returnRepository;
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.warrantyService = warrantyService;
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleRepository.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("Sale not found: " + saleId);
        }

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("Return period (30 days) has expired.");
        }

        // Validate products belong to the sale
        List<Product> productsToReturn = new ArrayList<>();
        for (String pid : productIds) {
            boolean found = false;
            for (Product p : sale.getproducts()) {
                if (p.getId().equals(pid)) {
                    productsToReturn.add(p);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Product " + pid + " does not belong to sale " + saleId);
            }
        }

        // Create return and calculate proportional refund
        String returnId = "R-" + System.currentTimeMillis();
        Return returnItem = new Return(returnId, sale, LocalDate.now(), productsToReturn, reason, 0.0);
        double refund = returnItem.calculateRefundAmount();

        // Cancel warranties for consoles + add warranty refund
        double warrantyRefund = 0.0;
        for (Product p : productsToReturn) {
            if (p instanceof Console) {
                warrantyRefund += warrantyService.cancelWarranties(p.getId(), saleId);
            }
        }

        // Restore stock
        for (Product p : productsToReturn) {
            if (p instanceof Accessory) {
                accessoryService.restoreStock(p.getId(), 1);
            } else {
                productService.restoreStock(p.getId(), 1);
            }
        }

        // Persist return (with total refund including warranty cost)
        Return finalReturn = new Return(returnId, sale, LocalDate.now(),
                productsToReturn, reason, refund + warrantyRefund);

        List<Return> all = returnRepository.loadAll();
        all.add(finalReturn);
        returnRepository.saveAll(all);
        return finalReturn;
    }

    public List<Return> viewAllReturns() {
        return returnRepository.loadAll();
    }

    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();
        for (Return r : returnRepository.loadAll()) {
            if (r.getSale().getcostumer().getId().equals(customerId)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (Return r : returnRepository.loadAll()) {
            if (r.getSale().getid().equals(saleId)) {
                result.add(r);
            }
        }
        return result;
    }

    public double calculateMonthlySales(int month, int year) {
        double total = 0.0;
        for (Sale s : saleRepository.loadAll()) {
            if (s.getdate().getMonthValue() == month && s.getdate().getYear() == year) {
                total += s.calculateTotal();
            }
        }
        return total;
    }

    public double calculateMonthlyReturns(int month, int year) {
        double total = 0.0;
        for (Return r : returnRepository.loadAll()) {
            if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
                total += r.getRefundAmount();
            }
        }
        return total;
    }

    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year) - calculateMonthlyReturns(month, year);
    }
}