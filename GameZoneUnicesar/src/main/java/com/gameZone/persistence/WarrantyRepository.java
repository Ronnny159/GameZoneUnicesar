package com.gameZone.persistence;

import com.gameZone.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WarrantyRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = DATA_DIRECTORY + "/warranties.csv";
    private static final String SEPARATOR = ";";

    private SaleRepository saleRepository;
    private ProductRepository productRepository;

    public WarrantyRepository(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Warranty warranty : warranties) {
                writer.write(toCsvLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving warranties", e);
        }
    }

    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return warranties;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Warranty warranty = fromCsvLine(line);
                if (warranty != null) {
                    warranties.add(warranty);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading warranties", e);
        }
        return warranties;
    }

    public Warranty findById(String warrantyId) {
        for (Warranty warranty : loadAll()) {
            if (warranty.getId().equals(warrantyId)) {
                return warranty;
            }
        }
        return null;
    }

    private String toCsvLine(Warranty warranty) {
        String type = (warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED";
        return String.join(SEPARATOR,
                type,
                warranty.getId(),
                warranty.getProduct().getId(),
                warranty.getSale().getid(),
                warranty.getStartDate().toString(),
                warranty.getEndDate().toString());
    }

    private Warranty fromCsvLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);

        String type = fields[0];
        String id = fields[1];
        Product product = productRepository.loadAll().stream()
                .filter(p -> p.getId().equals(fields[2]))
                .findFirst()
                .orElse(null);
        Sale sale = saleRepository.findById(fields[3]);
        LocalDate startDate = LocalDate.parse(fields[4]);

        if (product == null || sale == null) {
            return null;
        }

        return switch (type) {
            case "BASIC" -> new BasicWarranty(id, product, sale, startDate);
            case "EXTENDED" -> new ExtendedWarranty(id, product, sale, startDate);
            default -> null;
        };
    }
}