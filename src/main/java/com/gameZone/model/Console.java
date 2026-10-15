package com.gameZone.model;

/**
 * Represents a gaming console product.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private int generation;
    
    public Console(String id, String title, double price, int quantity,
                   String brand, String model, int generation) {
        super(id, title, price, quantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }
    
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getGeneration() { return generation; }
    
    @Override
    public Category getCategory() {
        return Category.CONSOLE;  
    }

    @Override
    public String getDescription() {
        return "Console: " + getTitle() + 
               " | Brand: " + brand + 
               " | Model: " + model + 
               " | Generation: " + generation + 
               " | Price: $" + getPrice() + 
               " | Stock: " + getQuantity();
    }

    @Override
    public boolean requiresBasicWarranty() {
        return true;
    }

    @Override
    public boolean supportsExtendedWarranty() {
        return true;
    }
}
