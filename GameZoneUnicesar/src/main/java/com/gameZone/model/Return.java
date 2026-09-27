package com.gameZone.model;

import java.time.LocalDate;
import java.util.List;

public class Return {
    private String id;
    private Sale sale;
    private LocalDate date;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    public Return(String id, Sale sale, LocalDate date, List<Product> returnedProducts, String reason, double refundAmount) {
        this.id = id;
        this.sale = sale;
        this.date = date;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = refundAmount;
    }
    public String getId() {
        return id;
    }
    public Sale getSale() {
        return sale;
    }
    public LocalDate getDate() {
        return date;
    }
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }
    public String getReason() {
        return reason;
    }
    public double getRefundAmount() {
        return refundAmount;
    }

    public double calculateRefundAmount(){
        double totalRefund = 0.0;
        for (Product product : returnedProducts) {
            totalRefund += product.getPrice();
        }
        return totalRefund;
    }

    public String generateReturnReceipt(){
        StringBuilder receipt = new StringBuilder();
        receipt.append("Return Receipt\n");
        receipt.append("Return ID: ").append(id).append("\n");
        receipt.append("Sale ID: ").append(sale.getid()).append("\n");
        receipt.append("Return Date: ").append(date).append("\n");
        receipt.append("Returned Products:\n");
        for (Product product : returnedProducts) {
            receipt.append("- ").append(product.getTitle()).append(" (Price: $").append(product.getPrice()).append(")\n");
        }
        receipt.append("Reason for Return: ").append(reason).append("\n");
        receipt.append("Total Refund Amount: $").append(refundAmount).append("\n");
        return receipt.toString();
    }
}
