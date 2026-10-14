package com.gameZone.model;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the store.
 * A sale involves a customer, a seller, and one or more products.
 * It stores the applied promotion, the discount amount, and the extended
 * warranty cost to compute the final total.
 */
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime date;
    private Customer customer;
    private Seller seller;
    private String saleId;
    private List<Product> products;
    private String appliedPromotionName;
    private double discountAmount;
    private double extendedWarrantyCost;

    public Sale(String saleId, Customer customer, Seller seller, List<Product> products) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.saleId = saleId;
        this.seller = seller;
        this.products = new ArrayList<>(products);
        this.appliedPromotionName = null;
        this.discountAmount = 0.0;
        this.extendedWarrantyCost = 0.0;
    }

    public Sale(String saleId, Customer customer, Seller seller, List<Product> products,
                String appliedPromotionName, double discountAmount) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.saleId = saleId;
        this.seller = seller;
        this.products = new ArrayList<>(products);
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
        this.extendedWarrantyCost = 0.0;
    }

    public String getId() { return saleId; }
    public Customer getCustomer() { return customer; }
    public Seller getSeller() { return seller; }
    public List<Product> getProducts() { return new ArrayList<>(products); }
    public LocalDateTime getDate() { return date; }
    public String getAppliedPromotionName() { return appliedPromotionName; }
    public double getDiscountAmount() { return discountAmount; }
    public double getExtendedWarrantyCost() { return extendedWarrantyCost; }

    public void setId(String saleId) { this.saleId = saleId; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setSeller(Seller seller) { this.seller = seller; }
    public void setProducts(List<Product> products) { this.products = new ArrayList<>(products); }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setAppliedPromotionName(String appliedPromotionName) { this.appliedPromotionName = appliedPromotionName; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setExtendedWarrantyCost(double extendedWarrantyCost) { this.extendedWarrantyCost = extendedWarrantyCost; }

    /**
     * Calculates the subtotal of the sale (sum of product list prices).
     *
     * @return the subtotal before discount and warranty cost
     */
    public double calculatePrice() {
        double sum = 0;
        for (Product product : products) {
            sum = sum + product.getPrice();
        }
        return sum;
    }

    /**
     * Calculates the final total of the sale:
     * subtotal − discount + extended warranty cost.
     *
     * @return the final total
     */
    public double calculateTotal() {
        return calculatePrice() - discountAmount + extendedWarrantyCost;
    }

    /**
     * Returns true if the sale is still within the 30-day return window.
     *
     * @return true if the sale can be returned
     */
    public boolean canBeReturned() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returnDeadline = date.plusDays(30);
        return now.isBefore(returnDeadline);
    }

    /**
     * Generates a formatted receipt showing subtotal, promotion, discount,
     * extended warranty cost, and final total.
     *
     * @return the receipt as a string
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Recibo de Venta\n");
        receipt.append("-------------------\n");
        receipt.append("ID Venta: ").append(saleId).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Cliente: ").append(customer.getName()).append("\n");
        receipt.append("Vendedor: ").append(seller.getName()).append("\n");
        receipt.append("Productos:\n");
        for (Product product : products) {
            receipt.append("- ").append(product.getTitle())
                   .append(" (Precio: $").append(product.getPrice()).append(")\n");
        }
        receipt.append("Subtotal: $").append(calculatePrice()).append("\n");
        receipt.append("Promoción aplicada: ")
               .append(appliedPromotionName == null ? "Ninguna" : appliedPromotionName).append("\n");
        receipt.append("Descuento: $").append(discountAmount).append("\n");
        receipt.append("Costo garantías extendidas: $").append(extendedWarrantyCost).append("\n");
        receipt.append("Total final: $").append(calculateTotal()).append("\n");
        return receipt.toString();
    }

    @Override
    public String toString() {
        return "Sale id: " + saleId + ", date: " + date + ", total: " + calculateTotal();
    }
}