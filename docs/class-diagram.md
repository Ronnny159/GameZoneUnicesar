# Class Diagram — GameZone Unicesar Integrated System

This diagram shows the complete class structure of the integrated system, including the four modules (accessories, promotions, warranties, returns) and their integration into the base system from Workshop 1.

```mermaid
classDiagram
    direction TB

    %% =====================================================
    %% MODEL LAYER
    %% =====================================================

    class Product {
        <<abstract>>
        - String id
        - String title
        - double price
        - int quantity
        + Product(String id, String title, double price, int quantity)
        + String getId()
        + String getTitle()
        + double getPrice()
        + int getQuantity()
        + void setTitle(String title)
        + void setPrice(double price)
        + void setQuantity(int quantity)
        + abstract String getDescription()
    }

    class VideoGame {
        - String platform
        - String genre
        - String ageRating
        + VideoGame(String id, String title, double price, int quantity, String platform, String genre, String ageRating)
        + String getPlatform()
        + String getGenre()
        + String getAgeRating()
        + String getDescription()
    }

    class Console {
        - String brand
        - String model
        - int generation
        + Console(String id, String title, double price, int quantity, String brand, String model, int generation)
        + String getBrand()
        + String getModel()
        + int getGeneration()
        + String getDescription()
    }

    class Accessory {
        <<abstract>>
        - List~String~ compatibleConsoles
        + Accessory(String id, String title, double price, int quantity)
        + List~String~ getCompatibleConsoles()
        + void setCompatibleConsoles(List~String~ compatibleConsoles)
        + void addCompatibleConsole(String consoleId)
        + boolean isCompatibleWith(String consoleId)
        + abstract String getAccessoryType()
        + String getDescription()
    }

    class Controller {
        - String connectionType
        + Controller(String id, String title, double price, int quantity, String connectionType)
        + String getConnectionType()
        + void setConnectionType(String connectionType)
        + String getAccessoryType()
        + String getDescription()
    }

    class Cable {
        - double length
        - String connectorType
        + Cable(String id, String title, double price, int quantity, double length, String connectorType)
        + double getLength()
        + void setLength(double length)
        + String getConnectorType()
        + void setConnectorType(String connectorType)
        + String getAccessoryType()
        + String getDescription()
    }

    class Memory {
        - int gigabytes
        - String memoryType
        + Memory(String id, String title, double price, int quantity, int gigabytes, String memoryType)
        + int getGigabytes()
        + void setGigabytes(int gigabytes)
        + String getMemoryType()
        + void setMemoryType(String memoryType)
        + String getAccessoryType()
        + String getDescription()
    }

    class Person {
        <<abstract>>
        - String id
        - String name
        - String phone
        + Person(String id, String name, String phone)
        + String getId()
        + void setId(String id)
        + String getName()
        + void setName(String name)
        + String getPhone()
        + void setPhone(String phone)
        + abstract String getRoleDescription()
    }

    class Customer {
        - String email
        + Customer(String id, String name, String phone, String email)
        + String getEmail()
        + void setEmail(String email)
        + String getRoleDescription()
    }

    class Seller {
        - String employeeCode
        - String shift
        + Seller(String id, String name, String phone, String employeeCode, String shift)
        + String getEmployeeCode()
        + void setEmployeeCode(String employeeCode)
        + String getShift()
        + void setShift(String shift)
        + String getRoleDescription()
    }

    class Sale {
        - LocalDateTime date
        - Customer customer
        - Seller seller
        - String saleid
        - List~Product~ products
        - String appliedPromotionName
        - double discountAmount
        - double extendedWarrantyCost
        + Sale(String saleid, Customer customer, Seller seller, List~Product~ products)
        + Sale(String saleid, Customer customer, Seller seller, List~Product~ products, String appliedPromotionName, double discountAmount)
        + String getid()
        + Customer getcostumer()
        + Seller getseller()
        + List~Product~ getproducts()
        + LocalDateTime getdate()
        + String getappliedPromotionName()
        + double getdiscountAmount()
        + double getExtendedWarrantyCost()
        + void setappliedPromotionName(String appliedPromotionName)
        + void setdiscountAmount(double discountAmount)
        + void setExtendedWarrantyCost(double extendedWarrantyCost)
        + double calculateprice()
        + double calculateTotal()
        + boolean canBeReturned()
        + String generateReceipt()
    }

    class Promotion {
        <<abstract>>
        - String id
        - String name
        - LocalDate startDate
        - LocalDate endDate
        + Promotion(String id, String name, LocalDate startDate, LocalDate endDate)
        + String getId()
        + String getName()
        + LocalDate getStartDate()
        + LocalDate getEndDate()
        + boolean isActive(LocalDate date)
        + abstract double calculateDiscount(Sale sale)
    }

    class PercentageDiscount {
        - double percentage
        + PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage)
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class CategoryDiscount {
        - String category
        - double percentage
        + CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String category, double percentage)
        + String getCategory()
        + void setCategory(String category)
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class BulkPurchaseDiscount {
        - int minQuantity
        - double percentage
        + BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minQuantity, double percentage)
        + int getMinQuantity()
        + void setMinQuantity(int minQuantity)
        + double getPercentage()
        + void setPercentage(double percentage)
        + double calculateDiscount(Sale sale)
    }

    class Warranty {
        <<abstract>>
        - String id
        - Product product
        - Sale sale
        - LocalDate startDate
        - LocalDate endDate
        + Warranty(String id, Product product, Sale sale, LocalDate startDate)
        + String getId()
        + Product getProduct()
        + Sale getSale()
        + LocalDate getStartDate()
        + LocalDate getEndDate()
        + abstract int getDurationInMonths()
        + abstract String getWarrantyType()
        + abstract double getAdditionalCost()
        + boolean isActive(LocalDate date)
        + String generateWarrantyCertificate()
    }

    class BasicWarranty {
        + BasicWarranty(String id, Product product, Sale sale, LocalDate startDate)
        + int getDurationInMonths()
        + String getWarrantyType()
        + double getAdditionalCost()
    }

    class ExtendedWarranty {
        + ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate)
        + int getDurationInMonths()
        + String getWarrantyType()
        + double getAdditionalCost()
    }

    class Return {
        - String id
        - Sale sale
        - LocalDate date
        - List~Product~ returnedProducts
        - String reason
        - double refundAmount
        + Return(String id, Sale sale, LocalDate date, List~Product~ returnedProducts, String reason, double refundAmount)
        + String getId()
        + Sale getSale()
        + LocalDate getDate()
        + List~Product~ getReturnedProducts()
        + String getReason()
        + double getRefundAmount()
        + double calculateRefundAmount()
        + String generateReturnReceipt()
    }

    %% =====================================================
    %% PERSISTENCE LAYER
    %% =====================================================

    class PersonRepository {
        + void saveCustomers(List~Customer~ customers)
        + List~Customer~ loadCustomers()
        + void saveSellers(List~Seller~ sellers)
        + List~Seller~ loadSellers()
    }

    class ProductRepository {
        + void saveAll(List~Product~ products)
        + List~Product~ loadAll()
        + Product findById(String productId)
    }

    class AccessoryRepository {
        + void saveAll(List~Accessory~ accessories)
        + List~Accessory~ loadAll()
    }

    class SaleRepository {
        + SaleRepository(ProductRepository productRepository, PersonRepository personRepository)
        + void saveAll(List~Sale~ sales)
        + List~Sale~ loadAll()
        + Sale findById(String saleId)
        + List~Sale~ findSalesByCustomer(String customerId)
        + List~Sale~ findSalesBySeller(String sellerId)
    }

    class PromotionRepository {
        + void saveAll(List~Promotion~ promotions)
        + List~Promotion~ loadAll()
    }

    class WarrantyRepository {
        + WarrantyRepository(SaleRepository saleRepository, ProductRepository productRepository)
        + void saveAll(List~Warranty~ warranties)
        + List~Warranty~ loadAll()
        + Warranty findById(String warrantyId)
    }

    class ReturnRepository {
        + ReturnRepository(SaleRepository saleRepository, ProductRepository productRepository)
        + void saveAll(List~Return~ returns)
        + List~Return~ loadAll()
    }

    %% =====================================================
    %% SERVICE LAYER
    %% =====================================================

    class PersonService {
        + PersonService(PersonRepository repository)
        + Customer registerCustomer(String id, String name, String phone, String email)
        + List~Customer~ listCustomers()
        + List~Seller~ listSellers()
        + Optional~Customer~ findCustomerById(String id)
        + Optional~Seller~ findSellerById(String id)
    }

    class ProductService {
        + ProductService(ProductRepository repository)
        + boolean registerProduct(Product product)
        + List~Product~ getAllProducts()
        + Product findById(String id)
        + boolean updateStock(String id, int newQuantity)
        + boolean hasEnoughStock(String id, int requestedAmount)
        + boolean reduceStock(String id, int amount)
        + boolean restoreStock(String productId, int quantity)
    }

    class AccessoryService {
        + AccessoryService(AccessoryRepository accessoryRepository)
        + Controller registerController(...)
        + Cable registerCable(...)
        + Memory registerMemory(...)
        + List~Accessory~ listAllAccessories()
        + List~Accessory~ listAccessoriesByType(String type)
        + List~Accessory~ findAccessoriesCompatibleWith(String consoleId)
        + Accessory findById(String id)
        + void updateStock(String accessoryId, int quantity)
        + void restoreStock(String accessoryId, int quantity)
    }

    class SaleService {
        + SaleService(SaleRepository saleRepository, ProductService productService, PersonService personService, AccessoryService accessoryService, PromotionService promotionService, WarrantyService warrantyService)
        + Sale registerSale(Customer customer, Seller seller, List~Product~ products, List~String~ productIdsWithExtendedWarranty)
        + List~Sale~ getAllSales()
        + List~Sale~ getCustomerHistory(String customerId)
        + List~Sale~ getSellerHistory(String sellerId)
        + double getTotalRevenue()
    }

    class PromotionService {
        + PromotionService(PromotionRepository promotionRepository)
        + PercentageDiscount registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage)
        + CategoryDiscount registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, String category, double percentage)
        + BulkPurchaseDiscount registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minQuantity, double percentage)
        + List~Promotion~ listAllPromotions()
        + List~Promotion~ listActivePromotions()
        + Promotion findById(String promotionId)
        + Promotion findBestPromotionFor(Sale sale)
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

    %% =====================================================
    %% UI LAYER
    %% =====================================================

    class Main {
        + static void main(String[] args)
    }

    class ConsoleMenu {
        + ConsoleMenu(PersonService, ProductService, AccessoryService, PromotionService, SaleService, WarrantyService, ReturnService, Scanner)
        + void show()
    }

    class PersonMenu {
        + PersonMenu(PersonService personService, Scanner input)
        + void show()
    }

    class ProductMenu {
        + ProductMenu(ProductService productService, Scanner input)
        + void show()
    }

    class AccessoryMenu {
        + AccessoryMenu(AccessoryService accessoryService, ProductService productService, Scanner input)
        + void show()
    }

    class PromotionMenu {
        + PromotionMenu(PromotionService promotionService, Scanner input)
        + void show()
    }

    class SaleMenu {
        + SaleMenu(SaleService saleService, PersonService personService, ProductService productService, AccessoryService accessoryService, WarrantyService warrantyService, Scanner input)
        + void show()
    }

    class WarrantyMenu {
        + WarrantyMenu(WarrantyService warrantyService, Scanner input)
        + void show()
    }

    class ReturnMenu {
        + ReturnMenu(ReturnService returnService, SaleService saleService, Scanner input)
        + void show()
        + void showMonthlyBalance()
    }

    class ConsoleUtils {
        + static int readInt(Scanner input, String prompt)
        + static double readDouble(Scanner input, String prompt)
        + static String readString(Scanner input, String prompt)
    }

    %% =====================================================
    %% INHERITANCE
    %% =====================================================

    Product <|-- VideoGame : extends
    Product <|-- Console : extends
    Product <|-- Accessory : extends
    Accessory <|-- Controller : extends
    Accessory <|-- Cable : extends
    Accessory <|-- Memory : extends

    Person <|-- Customer : extends
    Person <|-- Seller : extends

    Promotion <|-- PercentageDiscount : extends
    Promotion <|-- CategoryDiscount : extends
    Promotion <|-- BulkPurchaseDiscount : extends

    Warranty <|-- BasicWarranty : extends
    Warranty <|-- ExtendedWarranty : extends

    %% =====================================================
    %% ASSOCIATIONS / COMPOSITIONS
    %% =====================================================

    Sale *-- Customer : contains
    Sale *-- Seller : contains
    Sale *-- Product : contains
    Return --> Sale : references
    Return --> Product : returns
    Warranty --> Sale : associated with
    Warranty --> Product : covers

    %% =====================================================
    %% DEPENDENCIES (service → persistence)
    %% =====================================================

    PersonService ..> PersonRepository : depends on
    ProductService ..> ProductRepository : depends on
    AccessoryService ..> AccessoryRepository : depends on
    SaleService ..> SaleRepository : depends on
    SaleService ..> ProductService : uses
    SaleService ..> AccessoryService : uses
    SaleService ..> PersonService : uses
    SaleService ..> PromotionService : uses
    SaleService ..> WarrantyService : uses

    PromotionService ..> PromotionRepository : depends on

    WarrantyService ..> WarrantyRepository : depends on
    WarrantyService ..> ProductService : uses

    ReturnService ..> ReturnRepository : depends on
    ReturnService ..> SaleRepository : depends on
    ReturnService ..> ProductService : uses
    ReturnService ..> AccessoryService : uses
    ReturnService ..> WarrantyService : uses

    %% =====================================================
    %% UI → SERVICE
    %% =====================================================

    Main ..> ConsoleMenu : creates
    ConsoleMenu ..> PersonMenu : delegates
    ConsoleMenu ..> ProductMenu : delegates
    ConsoleMenu ..> AccessoryMenu : delegates
    ConsoleMenu ..> PromotionMenu : delegates
    ConsoleMenu ..> SaleMenu : delegates
    ConsoleMenu ..> WarrantyMenu : delegates
    ConsoleMenu ..> ReturnMenu : delegates

    PersonMenu ..> PersonService : uses
    ProductMenu ..> ProductService : uses
    AccessoryMenu ..> AccessoryService : uses
    AccessoryMenu ..> ProductService : uses
    PromotionMenu ..> PromotionService : uses
    SaleMenu ..> SaleService : uses
    SaleMenu ..> PersonService : uses
    SaleMenu ..> ProductService : uses
    SaleMenu ..> AccessoryService : uses
    SaleMenu ..> WarrantyService : uses
    WarrantyMenu ..> WarrantyService : uses
    ReturnMenu ..> ReturnService : uses
    ReturnMenu ..> SaleService : uses

    ConsoleMenu ..> ConsoleUtils : uses
    PersonMenu ..> ConsoleUtils : uses
    ProductMenu ..> ConsoleUtils : uses
    AccessoryMenu ..> ConsoleUtils : uses
    PromotionMenu ..> ConsoleUtils : uses
    SaleMenu ..> ConsoleUtils : uses
    WarrantyMenu ..> ConsoleUtils : uses
    ReturnMenu ..> ConsoleUtils : uses
```

## Legend

| Symbol | Meaning |
|--------|---------|
| `<\|--` | Inheritance (extends) |
| `*--` | Composition |
| `-->` | Association |
| `..>` | Dependency |
| `<<abstract>>` | Abstract class |

## Layer distribution

- **Model:** `Product`, `VideoGame`, `Console`, `Accessory`, `Controller`, `Cable`, `Memory`, `Person`, `Customer`, `Seller`, `Sale`, `Promotion`, `Warranty`, `Return`
- **Persistence:** `PersonRepository`, `ProductRepository`, `AccessoryRepository`, `SaleRepository`, `PromotionRepository`, `WarrantyRepository`, `ReturnRepository`
- **Service:** `PersonService`, `ProductService`, `AccessoryService`, `SaleService`, `PromotionService`, `WarrantyService`, `ReturnService`
- **UI:** `Main`, `ConsoleMenu`, `PersonMenu`, `ProductMenu`, `AccessoryMenu`, `PromotionMenu`, `SaleMenu`, `WarrantyMenu`, `ReturnMenu`, `ConsoleUtils`

## Integration notes

- **A1:** `CategoryDiscount` supports `ACCESSORY` as a valid category.
- **A2:** `WarrantyRepository` depends on `SaleRepository` and `ProductRepository` instead of `SaleService`, breaking the circular dependency.
- **A3:** `SaleService.registerSale` orchestrates subtotal → promotion → warranties → final total → inventory.
- **A4:** `ReturnService` injects `AccessoryService` to restore accessory stock.
- **A5:** `Return.calculateRefundAmount` applies the discount proportionally.
- **A6:** `ReturnService` provides `calculateMonthlySales`, `calculateMonthlyReturns`, and `generateMonthlyBalance`.
- **A7:** `WarrantyService.cancelWarranties` removes warranties on console returns.

## Module separation on the same diagram

Even though the diagram is unified, you can identify each module by its clusters:

- **Accessory module:** `Accessory`, `Controller`, `Cable`, `Memory`, `AccessoryRepository`, `AccessoryService`, `AccessoryMenu`.
- **Promotion module:** `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`, `PromotionRepository`, `PromotionService`, `PromotionMenu`.
- **Warranty module:** `Warranty`, `BasicWarranty`, `ExtendedWarranty`, `WarrantyRepository`, `WarrantyService`, `WarrantyMenu`.
- **Return module:** `Return`, `ReturnRepository`, `ReturnService`, `ReturnMenu`.