# Warranty Module Class Diagram — GameZone Unicesar

```mermaid
classDiagram
    direction TB

    class Warranty {
        <<abstract>>
        - String id
        - Product product
        - Sale sale
        - LocalDate startDate
        - LocalDate endDate
        + String getId()
        + Product getProduct()
        + Sale getSale()
        + LocalDate getStartDate()
        + LocalDate getEndDate()
        + int getDurationInMonths()*
        + String getWarrantyType()*
        + double getAdditionalCost()*
        + boolean isActive(LocalDate date)
        + String generateWarrantyCertificate()
    }

    class BasicWarranty {
        + int getDurationInMonths()
        + String getWarrantyType()
        + double getAdditionalCost()
    }

    class ExtendedWarranty {
        + int getDurationInMonths()
        + String getWarrantyType()
        + double getAdditionalCost()
    }

    class WarrantyRepository {
        + WarrantyRepository(SaleRepository saleRepository, ProductRepository productRepository)
        + void saveAll(List~Warranty~ warranties)
        + List~Warranty~ loadAll()
        + Warranty findById(String warrantyId)
    }

    class WarrantyService {
        + WarrantyService(WarrantyRepository warrantyRepository, ProductService productService)
        + BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate)
        + ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate)
        + Warranty findWarrantyByProduct(String productId, String saleId)
        + List~Warranty~ listAllWarranties()
        + List~Warranty~ listActiveWarranties()
        + List~Warranty~ listWarrantiesExpiringSoon(int daysAhead)
        + double cancelWarranties(String productId, String saleId)
    }

    class WarrantyMenu {
        + WarrantyMenu(WarrantyService warrantyService, Scanner input)
        + void show()
    }

    class Sale {
        - double extendedWarrantyCost
    }

    Warranty <|-- BasicWarranty : extends
    Warranty <|-- ExtendedWarranty : extends

    WarrantyService ..> WarrantyRepository : depends on
    WarrantyService ..> Warranty : manages
    WarrantyService ..> Sale : uses
    WarrantyRepository ..> SaleRepository : uses
    WarrantyRepository ..> ProductRepository : uses
    WarrantyMenu ..> WarrantyService : uses
    Sale ..> Warranty : generates

    note for Warranty "Abstract base class"
    note for BasicWarranty "6 months, cost 0"
    note for ExtendedWarranty "12 months, 10% of price"
```

## Relationships

| Relationship | Between | Type |
|--------------|---------|------|
| Inheritance | `Warranty` → `BasicWarranty`, `ExtendedWarranty` | `extends` |
| Dependency | `WarrantyService` → `WarrantyRepository` | uses |
| Dependency | `WarrantyService` → `ProductService` | uses |
| Dependency | `WarrantyRepository` → `SaleRepository` | uses (A2) |
| Dependency | `WarrantyRepository` → `ProductRepository` | uses (A2) |
| Association | `Warranty` → `Sale` | references |
| Association | `Warranty` → `Product` | covers |