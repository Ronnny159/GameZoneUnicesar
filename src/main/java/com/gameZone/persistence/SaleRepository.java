package com.gameZone.persistence;

import com.gameZone.model.Customer;
import com.gameZone.model.Product;
import com.gameZone.model.Sale;
import com.gameZone.model.Seller;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {

    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_PATH = DATA_DIRECTORY + "/sales.csv";
    private static final String SEPARATOR = ";";
    private static final String PRODUCT_DELIMITER = "\\|";
    private static final String PRODUCT_JOINER = "|";

    private ProductRepository productRepository;
    private PersonRepository personRepository;

    public SaleRepository(ProductRepository productRepository, PersonRepository personRepository) {
        this.productRepository = productRepository;
        this.personRepository = personRepository;
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving sales", e);
        }
    }

    public List<Sale> loadAll() {
        List<Sale> sales = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return sales;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Sale sale = fromCsvLine(line);
                if (sale != null) {
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading sales", e);
        }
        return sales;
    }

    public Sale findById(String saleId) {
        for (Sale sale : loadAll()) {
            if (sale.getid().equals(saleId)) {
                return sale;
            }
        }
        return null;
    }

    public List<Sale> findSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : loadAll()) {
            if (sale.getcostumer().getId().equals(customerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    public List<Sale> findSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : loadAll()) {
            if (sale.getseller().getId().equals(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    private String toCsvLine(Sale sale) {
        List<String> productIds = new ArrayList<>();
        for (Product product : sale.getproducts()) {
            productIds.add(product.getId());
        }
        String productIdsJoined = String.join(PRODUCT_JOINER, productIds);

        return String.join(SEPARATOR,
                sale.getid(),
                sale.getdate().toString(),
                sale.getcostumer().getId(),
                sale.getseller().getId(),
                productIdsJoined,
                sale.getappliedPromotionName() == null ? "" : sale.getappliedPromotionName(),
                String.valueOf(sale.getdiscountAmount()),
                String.valueOf(sale.getExtendedWarrantyCost()));
    }

    private Sale fromCsvLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);

        String saleId = fields[0];
        LocalDateTime date = LocalDateTime.parse(fields[1]);
        String customerId = fields[2];
        String sellerId = fields[3];
        String productsRaw = fields[4];
        String promotionName = fields[5].isBlank() ? null : fields[5];
        double discount = Double.parseDouble(fields[6]);
        double warrantyCost = fields.length > 7 ? Double.parseDouble(fields[7]) : 0.0;

        // Resolve customer
        Customer customer = null;
        for (Customer c : personRepository.loadCustomers()) {
            if (c.getId().equals(customerId)) {
                customer = c;
                break;
            }
        }

        // Resolve seller
        Seller seller = null;
        for (Seller s : personRepository.loadSellers()) {
            if (s.getId().equals(sellerId)) {
                seller = s;
                break;
            }
        }

        if (customer == null || seller == null) {
            return null;
        }

        // Resolve products
        List<Product> allProducts = productRepository.loadAll();
        List<Product> products = new ArrayList<>();
        if (!productsRaw.isBlank()) {
            for (String pid : productsRaw.split(PRODUCT_DELIMITER)) {
                for (Product p : allProducts) {
                    if (p.getId().equals(pid)) {
                        products.add(p);
                        break;
                    }
                }
            }
        }

        Sale sale = new Sale(saleId, customer, seller, products, promotionName, discount);
        sale.setdate(date);
        sale.setExtendedWarrantyCost(warrantyCost);
        return sale;
    }
}