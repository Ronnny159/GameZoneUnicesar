package com.gameZone.service;

import com.gameZone.model.*;
import com.gameZone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AccessoryService {

    private final AccessoryRepository accessoryRepository;
    private List<Accessory> accessories;

    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
        this.accessories = new ArrayList<>(accessoryRepository.loadAll());
    }

    public Controller registerController(String id, String title, double price, int quantity,
                                          String connectionType, List<String> compatibleConsoles) {
        Controller controller = new Controller(id, title, price, quantity, connectionType);
        if (compatibleConsoles != null) {
            controller.setCompatibleConsoles(new ArrayList<>(compatibleConsoles));
        }
        accessories.add(controller);
        persist();
        return controller;
    }

    public Cable registerCable(String id, String title, double price, int quantity,
                                double length, String connectorType) {
        Cable cable = new Cable(id, title, price, quantity, length, connectorType);
        accessories.add(cable);
        persist();
        return cable;
    }

    public Memory registerMemory(String id, String title, double price, int quantity,
                                  int gigabytes, String memoryType, List<String> compatibleConsoles) {
        Memory memory = new Memory(id, title, price, quantity, gigabytes, memoryType);
        if (compatibleConsoles != null) {
            memory.setCompatibleConsoles(new ArrayList<>(compatibleConsoles));
        }
        accessories.add(memory);
        persist();
        return memory;
    }

    public List<Accessory> listAllAccessories() {
        return new ArrayList<>(accessories);
    }

    public List<Accessory> listAccessoriesByType(String type) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory.getAccessoryType().equalsIgnoreCase(type)) {
                result.add(accessory);
            }
        }
        return result;
    }

    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory.isCompatibleWith(consoleId)) {
                result.add(accessory);
            }
        }
        return result;
    }

    public Accessory findById(String id) {
        Optional<Accessory> found = accessories.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
        return found.orElseThrow(() ->
                new NoSuchElementException("No accessory found with id: " + id));
    }

    public void updateStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        int newQuantity = accessory.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new IllegalStateException("Insufficient stock for accessory: " + accessoryId);
        }
        accessory.setQuantity(newQuantity);
        persist();
    }

    public void restoreStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        accessory.setQuantity(accessory.getQuantity() + quantity);
        persist();
    }

    private void persist() {
        accessoryRepository.saveAll(accessories);
    }
}