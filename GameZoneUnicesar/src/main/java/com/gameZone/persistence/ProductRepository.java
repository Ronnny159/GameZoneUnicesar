package com.gameZone.persistence;

import com.gameZone.model.*;
import com.gameZone.model.Console;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = DATA_DIRECTORY + "/products.csv";
    private static final String SEPARATOR = ";";

    public ProductRepository() {
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    public void saveAll(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product product : products) {
                writer.write(toCsvLine(product));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving products", e);
        }
    }

    public List<Product> loadAll() {
        List<Product> products = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return products;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Product product = fromCsvLine(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading products", e);
        }
        return products;
    }

    private String toCsvLine(Product product) {
        StringBuilder line = new StringBuilder();
        if (product instanceof VideoGame vg) {
            line.append("VIDEOGAME").append(SEPARATOR)
                    .append(vg.getId()).append(SEPARATOR)
                    .append(vg.getTitle()).append(SEPARATOR)
                    .append(vg.getPrice()).append(SEPARATOR)
                    .append(vg.getQuantity()).append(SEPARATOR)
                    .append(vg.getPlatform()).append(SEPARATOR)
                    .append(vg.getGenre()).append(SEPARATOR)
                    .append(vg.getAgeRating());
        } else if (product instanceof Console c) {
            line.append("CONSOLE").append(SEPARATOR)
                    .append(c.getId()).append(SEPARATOR)
                    .append(c.getTitle()).append(SEPARATOR)
                    .append(c.getPrice()).append(SEPARATOR)
                    .append(c.getQuantity()).append(SEPARATOR)
                    .append(c.getBrand()).append(SEPARATOR)
                    .append(c.getModel()).append(SEPARATOR)
                    .append(c.getGeneration());
        }
        return line.toString();
    }

    private Product fromCsvLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int quantity = Integer.parseInt(fields[4]);

        return switch (type) {
            case "VIDEOGAME" -> new VideoGame(id, title, price, quantity,
                    fields[5], fields[6], fields[7]);
            case "CONSOLE" -> new Console(id, title, price, quantity,
                    fields[5], fields[6], Integer.parseInt(fields[7]));
            default -> null;
        };
    }
}