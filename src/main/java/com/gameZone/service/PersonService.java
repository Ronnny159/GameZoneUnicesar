package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.PersonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Contains the business rules for the person module: customer registration
 * and read access to both lists.
 * This is the only class in the module authorized to invoke the
 * {@link PersonRepository}; the UI layer must always go through this service
 * instead of accessing persistence directly.
 */
public class PersonService {

    private final PersonRepository repository;
    private final List<Customer> customers;
    private final List<Seller> sellers;

    /**
     * Creates the service, loading previously persisted data and preloading
     * three default sellers the first time the application runs (when the
     * sellers file does not exist yet or is empty).
     *
     * @param repository the persistence class used to read/write person data
     */
    public PersonService(PersonRepository repository) {
        this.repository = repository;
        this.customers = repository.loadCustomers();
        this.sellers = repository.loadSellers();

        if (this.sellers.isEmpty()) {
            this.sellers.add(new Seller("V-001", "Ana Gomez", "3001234567", "EMP001", "Mañana"));
            this.sellers.add(new Seller("V-002", "Luis Perez", "3007654321", "EMP002", "Tarde"));
            this.sellers.add(new Seller("V-003", "Carla Diaz", "3009876543", "EMP003", "Noche"));
            repository.saveSellers(this.sellers);
        }
    }

    /**
     * Registers a new customer, validating that the id is not already in use,
     * and then persists the updated list.
     *
     * @param id    unique customer identifier
     * @param name  full customer name
     * @param phone contact phone number
     * @param email contact email
     * @return the newly registered customer
     * @throws IllegalArgumentException if a customer with the same id already exists
     */
    public Customer registerCustomer(String id, String name, String phone, String email) {
        if (findCustomerById(id).isPresent()) {
            throw new IllegalArgumentException("A customer with id " + id + " already exists");
        }
        Customer customer = new Customer(id, name, phone, email);
        customers.add(customer);
        repository.saveCustomers(customers);
        return customer;
    }

    public List<Customer> listCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Seller> listSellers() {
        return Collections.unmodifiableList(sellers);
    }

    /**
     * Finds a customer by unique identifier.
     *
     * @param id the customer identifier to search for
     * @return an {@link Optional} with the customer if found
     */
    public Optional<Customer> findCustomerById(String id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds a seller by unique identifier.
     *
     * @param id the seller identifier to search for
     * @return an {@link Optional} with the seller if found
     */
    public Optional<Seller> findSellerById(String id) {
        return sellers.stream()
                .filter(seller -> seller.getId().equals(id))
                .findFirst();
    }
}