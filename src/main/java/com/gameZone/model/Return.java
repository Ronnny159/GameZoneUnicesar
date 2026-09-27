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

    public String getId() { return id; }
    public Sale getSale() { return sale; }
    public LocalDate getDate() { return date; }
    public List<Product> getReturnedProducts() { return returnedProducts; }
    public String getReason() { return reason; }
    public double getRefundAmount() { return refundAmount; }

    public double calculateRefundAmount() {
        double subtotal = sale.calculateprice();
        double totalRefund = 0.0;

        for (Product product : returnedProducts) {
            double proportional = product.getPrice();
            if (subtotal > 0 && sale.getdiscountAmount() > 0) {
                proportional = product.getPrice() * (1 - sale.getdiscountAmount() / subtotal);
            }
            totalRefund += proportional;
        }

        this.refundAmount = totalRefund;
        return totalRefund;
    }

    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Recibo de Devolución\n");
        receipt.append("-------------------\n");
        receipt.append("ID Devolución: ").append(id).append("\n");
        receipt.append("ID Venta: ").append(sale.getid()).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Productos devueltos:\n");

        double subtotal = sale.calculateprice();
        for (Product product : returnedProducts) {
            double proportional = product.getPrice();
            if (subtotal > 0 && sale.getdiscountAmount() > 0) {
                proportional = product.getPrice() * (1 - sale.getdiscountAmount() / subtotal);
            }
            receipt.append("- ").append(product.getTitle())
                   .append(" | Precio lista: $").append(product.getPrice())
                   .append(" | Reembolso: $").append(proportional).append("\n");
        }

        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append("Monto total reembolsado: $").append(refundAmount).append("\n");
        return receipt.toString();
    }
}