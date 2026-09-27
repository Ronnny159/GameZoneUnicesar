package com.gameZone.persistence;

import com.gameZone.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = DATA_DIRECTORY + "/returns.csv";
    private static final String SEPARATOR = ";";
    private static final String PRODUCT_DELIMITER = "\\|";
    private static final String PRODUCT_JOINER = "|";

    private SaleRepository saleRepository;
    private ProductRepository productRepository;

    public ReturnRepository(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Return returnItem : returns) {
                writer.write(toCsvLine(returnItem));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving returns", e);
        }
    }

    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return returns;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Return returnItem = fromCsvLine(line);
                if (returnItem != null) {
                    returns.add(returnItem);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading returns", e);
        }
        return returns;
    }

    private String toCsvLine(Return returnItem) {
        List<String> productIds = new ArrayList<>();
        for (Product product : returnItem.getReturnedProducts()) {
            productIds.add(product.getId());
        }

        return String.join(SEPARATOR,
                returnItem.getId(),
                returnItem.getSale().getid(),
                returnItem.getDate().toString(),
                String.join(PRODUCT_JOINER, productIds),
                returnItem.getReason(),
                String.valueOf(returnItem.getRefundAmount()));
    }

    private Return fromCsvLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);

        String id = fields[0];
        Sale sale = saleRepository.findById(fields[1]);
        LocalDate date = LocalDate.parse(fields[2]);
        String productsRaw = fields[3];
        String reason = fields[4];
        double refund = Double.parseDouble(fields[5]);

        List<Product> products = new ArrayList<>();
        if (!productsRaw.isBlank()) {
            for (String pid : productsRaw.split(PRODUCT_DELIMITER)) {
                for (Product p : productRepository.loadAll()) {
                    if (p.getId().equals(pid)) {
                        products.add(p);
                        break;
                    }
                }
            }
        }

        return new Return(id, sale, date, products, reason, refund);
    }
}