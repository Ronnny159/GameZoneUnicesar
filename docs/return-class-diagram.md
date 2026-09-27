# Return Module Class Diagram — GameZone Unicesar

```mermaid
classDiagram
    direction TB

    class Return {
        - String id
        - Sale sale
        - LocalDate date
        - List~Product~ returnedProducts
        - String reason
        - double refundAmount
        + String getId()
        + Sale getSale()
        + LocalDate getDate()
        + List~Product~ getReturnedProducts()
        + String getReason()
        + double getRefundAmount()
        + double calculateRefundAmount()
        + String generateReturnReceipt()
    }

    class ReturnRepository {
        + ReturnRepository(SaleRepository saleRepository, ProductRepository productRepository)
        + void saveAll(List~Return~ returns)
        + List~Return~ loadAll()
    }

    class ReturnService {
        + ReturnService(ReturnRepository returnRepository, SaleRepository saleRepository, ProductService productService, AccessoryService accessoryService, WarrantyService warrantyService)
        + Return registerReturn(String saleId, List~String~ productIds, String reason)
        + List~Return~ viewAllReturns()
        + List~Return~ viewReturnsByCustomer(String customerId)
        + List~Return~ viewReturnsBySale(String saleId)
        + double calculateMonthlySales(int month, int year)
        + double calculateMonthlyReturns(int month, int year)
        + double generateMonthlyBalance(int month, int year)
    }

    class ReturnMenu {
        + ReturnMenu(ReturnService returnService, SaleService saleService, Scanner input)
        + void show()
        + void showMonthlyBalance()
    }

    class Sale {
        + boolean canBeReturned()
    }

    class ProductService {
        + boolean restoreStock(String productId, int quantity)
    }

    class AccessoryService {
        + void restoreStock(String accessoryId, int quantity)
    }

    class WarrantyService {
        + double cancelWarranties(String productId, String saleId)
    }

    Return --> Sale : references
    Return --> Product : returns

    ReturnService ..> ReturnRepository : depends on
    ReturnService ..> SaleRepository : depends on
    ReturnService ..> ProductService : uses
    ReturnService ..> AccessoryService : uses (A4)
    ReturnService ..> WarrantyService : uses (A7)
    ReturnService ..> Sale : validates
    ReturnMenu ..> ReturnService : uses
    ReturnMenu ..> SaleService : uses

    note for Return "Unidirectional association with Sale"
    note for ReturnService "Orchestrates return, stock, warranty"
```

## Relationships

| Relationship | Between | Type |
|--------------|---------|------|
| Association | `Return` → `Sale` | references |
| Association | `Return` → `Product` | returns |
| Dependency | `ReturnService` → `ReturnRepository` | uses |
| Dependency | `ReturnService` → `SaleRepository` | uses |
| Dependency | `ReturnService` → `ProductService` | uses (A4) |
| Dependency | `ReturnService` → `AccessoryService` | uses (A4) |
| Dependency | `ReturnService` → `WarrantyService` | uses (A7) |
| Dependency | `ReturnMenu` → `ReturnService` | uses |
| Dependency | `ReturnMenu` → `SaleService` | uses |