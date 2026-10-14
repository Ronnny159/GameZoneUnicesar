package com.gameZone.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a return of one or more products from a previous sale.
 * A return references the original sale, the returned products, the reason,
 * and the refund amount. The refund is proportional to the discount applied
 * to the original sale.
 */
public class Return implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Sale sale;
    private LocalDate date;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    public Return(String id, Sale sale, LocalDate date, List<Product> returnedProducts,
                  String reason, double refundAmount) {
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

    /**
     * Calculates the proportional refund for a single product, based on the
     * discount applied to the original sale.
     *
     * @param product the returned product
     * @return the proportional refund amount
     */
    private double calculateProportionalRefund(Product product) {
        double subtotal = sale.calculatePrice();
        if (subtotal > 0 && sale.getDiscountAmount() > 0) {
            return product.getPrice() * (1 - sale.getDiscountAmount() / subtotal);
        }
        return product.getPrice();
    }

    /**
     * Calculates the total refund amount for the returned products.
     *
     * @return the total refund
     */
    public double calculateRefundAmount() {
        double totalRefund = 0.0;
        for (Product product : returnedProducts) {
            totalRefund += calculateProportionalRefund(product);
        }
        this.refundAmount = totalRefund;
        return totalRefund;
    }

    /**
     * Generates a formatted return receipt showing list price, proportional
     * discount, and refund amount per item.
     *
     * @return the receipt as a string
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Recibo de Devolución\n");
        receipt.append("-------------------\n");
        receipt.append("ID Devolución: ").append(id).append("\n");
        receipt.append("ID Venta: ").append(sale.getId()).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Productos devueltos:\n");

        for (Product product : returnedProducts) {
            double proportional = calculateProportionalRefund(product);
            double discount = product.getPrice() - proportional;
            receipt.append("- ").append(product.getTitle())
                   .append(" | Precio lista: $").append(product.getPrice())
                   .append(" | Descuento: $").append(discount)
                   .append(" | Reembolso: $").append(proportional).append("\n");
        }

        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append("Monto total reembolsado: $").append(refundAmount).append("\n");
        return receipt.toString();
    }
}