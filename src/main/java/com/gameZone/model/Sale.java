package com.gameZone.model;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sale implements Serializable {

    private LocalDateTime date;
    private Customer customer;
    private Seller seller;
    private String saleid;
    private List<Product> products;
    private String appliedPromotionName;
    private double discountAmount;
    private double extendedWarrantyCost;

    public Sale(String saleid, Customer customer, Seller seller, List<Product> products) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.saleid = saleid;
        this.seller = seller;
        this.products = new ArrayList<>(products);
        this.appliedPromotionName = null;
        this.discountAmount = 0.0;
        this.extendedWarrantyCost = 0.0;
    }

    public Sale(String saleid, Customer customer, Seller seller, List<Product> products,
                String appliedPromotionName, double discountAmount) {
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.saleid = saleid;
        this.seller = seller;
        this.products = new ArrayList<>(products);
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
        this.extendedWarrantyCost = 0.0;
    }

    public String getid() { return saleid; }
    public Customer getcostumer() { return customer; }
    public Seller getseller() { return seller; }
    public List<Product> getproducts() { return new ArrayList<>(products); }
    public LocalDateTime getdate() { return date; }
    public String getappliedPromotionName() { return appliedPromotionName; }
    public double getdiscountAmount() { return discountAmount; }
    public double getExtendedWarrantyCost() { return extendedWarrantyCost; }

    public void setid(String saleid) { this.saleid = saleid; }
    public void setcustomer(Customer customer) { this.customer = customer; }
    public void setseller(Seller seller) { this.seller = seller; }
    public void setproducts(List<Product> products) { this.products = new ArrayList<>(products); }
    public void setdate(LocalDateTime date) { this.date = date; }
    public void setappliedPromotionName(String appliedPromotionName) { this.appliedPromotionName = appliedPromotionName; }
    public void setdiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setExtendedWarrantyCost(double extendedWarrantyCost) { this.extendedWarrantyCost = extendedWarrantyCost; }

    public double calculateprice() {
        double sum = 0;
        for (Product product : products) {
            sum = sum + product.getPrice();
        }
        return sum;
    }

    public double calculateTotal() {
        return calculateprice() - discountAmount + extendedWarrantyCost;
    }

    public boolean canBeReturned() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returnDeadline = date.plusDays(30);
        return now.isBefore(returnDeadline);
    }

    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Recibo de Venta\n");
        receipt.append("-------------------\n");
        receipt.append("ID Venta: ").append(saleid).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Cliente: ").append(customer.getName()).append("\n");
        receipt.append("Vendedor: ").append(seller.getName()).append("\n");
        receipt.append("Productos:\n");
        for (Product product : products) {
            receipt.append("- ").append(product.getTitle())
                   .append(" (Precio: $").append(product.getPrice()).append(")\n");
        }
        receipt.append("Subtotal: $").append(calculateprice()).append("\n");
        receipt.append("Promoción aplicada: ")
               .append(appliedPromotionName == null ? "Ninguna" : appliedPromotionName).append("\n");
        receipt.append("Descuento: $").append(discountAmount).append("\n");
        receipt.append("Costo garantías extendidas: $").append(extendedWarrantyCost).append("\n");
        receipt.append("Total final: $").append(calculateTotal()).append("\n");
        return receipt.toString();
    }

    @Override
    public String toString() {
        return "Sale id: " + saleid + ", date: " + date + ", total: " + calculateTotal();
    }
}