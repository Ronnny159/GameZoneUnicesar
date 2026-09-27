package com.gameZone.persistence;

import com.gameZone.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = DATA_DIRECTORY + "/promotions.csv";
    private static final String SEPARATOR = ";";

    public PromotionRepository() {
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Promotion promotion : promotions) {
                writer.write(toCsvLine(promotion));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving promotions", e);
        }
    }

    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return promotions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Promotion promotion = fromCsvLine(line);
                if (promotion != null) {
                    promotions.add(promotion);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading promotions", e);
        }
        return promotions;
    }

    private String toCsvLine(Promotion promotion) {
        StringBuilder line = new StringBuilder();
        line.append(promotion.getId()).append(SEPARATOR)
                .append(promotion.getName()).append(SEPARATOR)
                .append(promotion.getStartDate()).append(SEPARATOR)
                .append(promotion.getEndDate()).append(SEPARATOR);

        if (promotion instanceof PercentageDiscount pd) {
            line.append("PERCENTAGE").append(SEPARATOR)
                    .append(pd.getPercentage());
        } else if (promotion instanceof CategoryDiscount cd) {
            line.append("CATEGORY").append(SEPARATOR)
                    .append(cd.getCategory()).append(SEPARATOR)
                    .append(cd.getPercentage());
        } else if (promotion instanceof BulkPurchaseDiscount bd) {
            line.append("BULK").append(SEPARATOR)
                    .append(bd.getMinQuantity()).append(SEPARATOR)
                    .append(bd.getPercentage());
        }

        return line.toString();
    }

    private Promotion fromCsvLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);

        String id = fields[0];
        String name = fields[1];
        LocalDate startDate = LocalDate.parse(fields[2]);
        LocalDate endDate = LocalDate.parse(fields[3]);
        String type = fields[4];

        return switch (type) {
            case "PERCENTAGE" -> new PercentageDiscount(id, name, startDate, endDate,
                    Double.parseDouble(fields[5]));
            case "CATEGORY" -> new CategoryDiscount(id, name, startDate, endDate,
                    fields[5], Double.parseDouble(fields[6]));
            case "BULK" -> new BulkPurchaseDiscount(id, name, startDate, endDate,
                    Integer.parseInt(fields[5]), Double.parseDouble(fields[6]));
            default -> null;
        };
    }
}