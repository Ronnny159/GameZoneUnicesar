/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    
    public Sale(String saleid, Customer customer, Seller seller, List<Product> products, String appliedPromotionName, double discountAmount) {
        this.date = LocalDateTime.now(); //I used AI for getting this specific function
        this.customer = customer;
        this.saleid = saleid;
        this.seller = seller;
        this.products = new ArrayList<>(products); //AI used to detect logic problems
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
    }
    
    public String getid(){ return saleid;}
    public Customer getcostumer(){return customer;}
    public Seller getseller(){return seller;}
    public List<Product> getproducts(){return new ArrayList<>(products);}
    public LocalDateTime getdate(){return date;}
    public String getappliedPromotionName(){return appliedPromotionName;}
    public double getdiscountAmount(){return discountAmount;}

    //SETTERS
    public void setid(String saleid){ this.saleid = saleid;}
    public void setcustomer(Customer customer){this.customer = customer;}
    public void setseller(Seller seller){this.seller = seller;}
    public void setproducts(List<Product> products){this.products = new ArrayList<>(products);}
    public void setdate(LocalDateTime date){this.date = date;}
    public void setappliedPromotionName(String appliedPromotionName){this.appliedPromotionName = appliedPromotionName;}
    public void setdiscountAmount(double discountAmount){this.discountAmount = discountAmount;}

    public double calculateprice(){
    
        double sum = 0;
        for (Product product: products){
        
            sum = sum + product.getPrice();
        
        }
        return sum;
    }
    
    public boolean canBeReturned(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returnDeadline = date.plusDays(30);
        return now.isBefore(returnDeadline);
    }
    
    public String generateReceipt(){
        StringBuilder receipt = new StringBuilder();
        receipt.append("Sale Receipt\n");
        receipt.append("Sale ID: ").append(saleid).append("\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Customer: ").append(customer.getName()).append("\n");
        receipt.append("Seller: ").append(seller.getName()).append("\n");
        receipt.append("Products:\n");
        for (Product product : products) {
            receipt.append("- ").append(product.getTitle()).append(" (Price: $").append(product.getPrice()).append(")\n");
        }
        receipt.append("Applied Promotion: ").append(appliedPromotionName).append("\n");
        receipt.append("Discount Amount: $").append(discountAmount).append("\n");
        receipt.append("Total Price: $").append(calculateprice() - discountAmount).append("\n");
        return receipt.toString();
    }
}

