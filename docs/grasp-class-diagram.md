# GRASP Class Diagram — Before and After

## Before corrections

```mermaid
classDiagram
    class SaleService {
        - SaleRepository
        - ProductService
        - PersonService
        - AccessoryService
        - PromotionService
        - WarrantyService
        + registerSale(...)
    }
    note for SaleService "V-02, V-04, V-05, V-07"

    class ReturnService {
        - ReturnRepository
        - SaleRepository
        - ProductService
        - AccessoryService
        - WarrantyService
        + registerReturn(...)
    }
    note for ReturnService "V-07"

    class SaleMenu {
        + registerSale()
    }
    note for SaleMenu "V-01"

    class Sale {
        + generateReceipt()
        + calculatePrice()
        + canBeReturned()
    }
    note for Sale "V-03, V-05"

    class Product {
        <<abstract>>
        + getCategory() String
    }
    note for Product "V-02, V-08"

    class PromotionService {
        + registerCategoryDiscount(...)
    }
    note for PromotionService "V-08"

    class PersonService {
        + PersonService(repo)
    }
    note for PersonService "V-09"

    SaleService ..> ProductService
    SaleService ..> AccessoryService
    SaleService ..> PromotionService
    SaleService ..> WarrantyService
    ReturnService ..> ProductService
    ReturnService ..> AccessoryService