package com.gameZone.model;

/**
 * Represents a customer of the GameZone Unicesar store: a person who buys
 * products. In addition to the attributes shared with {@link Person}, a
 * customer has an email used for contact and (optionally) notifications.
 * <p>
 * The purchase history is NOT stored inside this class, on purpose, to avoid
 * duplicating data that already exists in the sales module. The sales service
 * is responsible for querying the customer's purchase history by id, keeping
 * a single source of truth.
 */
public class Customer extends Person {

    private String email;

    public Customer(String id, String name, String phone, String email) {
        super(id, name, phone);
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getRoleDescription() {
        return "Customer: " + getName() + " | Email: " + email;
    }
}