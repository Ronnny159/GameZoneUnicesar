# Promotion Module Class Diagram — GameZone Unicesar

```mermaid
classDiagram
    direction TB

    class Promotion {
        <<abstract>>
        - String id
        - String name
        - LocalDate startDate
        - LocalDate endDate
        + String getId()
        + String getName()
        + LocalDate getStartDate()
        + LocalDate getEndDate()
        + boolean isActive(LocalDate date)
        + double calculateDiscount(Sale sale)*
    }

    class PercentageDiscount {
        - double percentage
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class CategoryDiscount {
        - String category
        - double percentage
        + String getCategory()
        + void setCategory(String category)
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class BulkPurchaseDiscount {
        - int minQuantity
        - double percentage
        + int getMinQuantity()
        + void setMinQuantity(int minQuantity)
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class PromotionRepository {
        + void saveAll(List~Promotion~ promotions)
        + List~Promotion~ loadAll()
    }

    class PromotionService {
        + PercentageDiscount registerPercentageDiscount(...)
        + CategoryDiscount registerCategoryDiscount(...)
        + BulkPurchaseDiscount registerBulkPurchaseDiscount(...)
        + List~Promotion~ listAllPromotions()
        + List~Promotion~ listActivePromotions()
        + Promotion findById(String promotionId)
        + Promotion findBestPromotionFor(Sale sale)
    }

    class PromotionMenu {
        + PromotionMenu(PromotionService promotionService, Scanner input)
        + void show()
    }

    class Sale {
        - String appliedPromotionName
        - double discountAmount
    }

    Promotion <|-- PercentageDiscount : extends
    Promotion <|-- CategoryDiscount : extends
    Promotion <|-- BulkPurchaseDiscount : extends

    PromotionService ..> PromotionRepository : depends on
    PromotionService ..> Promotion : manages
    PromotionService ..> Sale : evaluates
    PromotionMenu ..> PromotionService : uses
    Sale ..> Promotion : stores result of

    note for Promotion "Abstract base class"
    note for PercentageDiscount "Discount on total"
    note for CategoryDiscount "Discount on a category"
    note for BulkPurchaseDiscount "Discount by quantity"
```

## Relationships

| Relationship | Between | Type |
|--------------|---------|------|
| Inheritance | `Promotion` → `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | `extends` |
| Dependency | `PromotionService` → `PromotionRepository` | uses |
| Dependency | `PromotionService` → `Sale` | evaluates |
| Dependency | `PromotionMenu` → `PromotionService` | uses |
| Association | `Sale` → `Promotion` (stores `appliedPromotionName`) | stores result |