# Analysis — GameZone Unicesar Integrated System

**Team:** GameZone Unicesar  
**Course:** Programming III  
**Program:** Systems Engineering — Universidad Popular del Cesar  
**Instructor:** Ing. Esp. Alfredo Bautista  

---

## Table of Contents

1. [Base System (Workshop 1)](#section-1--base-system-workshop-1)
2. [Accessory Module](#section-2--accessory-module)
3. [Promotion Module](#section-3--promotion-module)
4. [Warranty Module](#section-4--warranty-module)
5. [Return Module](#section-5--return-module)
6. [Integration Adjustments (A1–A7)](#section-6--integration-adjustments-a1a7)

---

## Section 1 — Base System (Workshop 1)

### 1.1 Attributes common to all people

**Question:** What attributes are common to all people who interact with the store, and which are specific to each type of person? How is this distinction reflected in a class hierarchy?

**Answer:**

All people share basic personal information through the abstract `Person` class:

- `id`: unique identifier
- `name`: full name
- `phone`: contact phone number

Specific attributes:

- **Customer:** `email`
- **Seller:** `employeeCode`, `shift`

This is reflected with an inheritance hierarchy: `Person` is abstract and holds common attributes and the abstract method `getRoleDescription()`. `Customer` and `Seller` extend `Person` and add their specific attributes. This promotes code reuse, polymorphism, and a consistent interface across all person types.

---

### 1.2 Should a generic Person class exist?

**Question:** Should there be a class that represents a "generic person" without specifying their role? Why or why not?

**Answer:**

No. `Person` must be **abstract** because:

1. In the business domain, every person has a specific role (customer or seller).
2. Instantiating a role-less person would create incomplete objects.
3. The abstract class declares a common contract (`getRoleDescription()`) that every subclass must implement.

**Implication:** `new Person(...)` is not allowed. The system only works with `Customer` or `Seller` instances.

---

### 1.3 Common and specific product attributes

**Question:** What characteristics do all products share, regardless of type? What characteristics are specific to each type?

**Answer:**

Common attributes (`Product` base class):

- `id`, `title`, `price`, `quantity`

Specific attributes:

- **VideoGame:** `platform`, `genre`, `ageRating`
- **Console:** `brand`, `model`, `generation`

---

### 1.4 Product descriptions

**Question:** How should `getDescription()` be declared in the base class?

**Answer:**

As an abstract method:

```java
public abstract String getDescription();
```

This forces every subclass to provide its own implementation while the base class guarantees the contract.

---

## Section 2 — Accessory Module

### 2.1 Should accessories extend the product hierarchy?

**Question:** Should accessories be fused into the existing product hierarchy or form a new independent hierarchy?

**Answer:**

They should **extend the existing `Product` hierarchy**. Reasons:

- Reuse of stock, price, and description logic.
- Polymorphism: accessories can be treated as `Product` in sales.
- Integration with the same persistence and service layer without duplication.

`Accessory` is an abstract subclass of `Product`.

---

### 2.2 Common and specific accessory attributes

**Question:** What attributes are common to the three types of accessories, and which are specific?

**Answer:**

Common (inherited from `Product`):

- `id`, `title`, `price`, `quantity`

Specific to all accessories (`Accessory`):

- `compatibleConsoles: List<String>` (console IDs)

Specific per type:

- **Controller:** `connectionType` (Wired, Wireless)
- **Cable:** `length`, `connectorType` (HDMI, USB, Optical)
- **Memory:** `gigabytes`, `memoryType` (SD, microSD)

This is reflected with `Accessory` as an abstract base class and `Controller`, `Cable`, `Memory` as concrete subclasses.

---

### 2.3 Compatibility relationship

**Question:** How is the compatibility between accessory and console represented in design and persistence? Is it an attribute of accessory, console, or both?

**Answer:**

Compatibility is modeled as an **association** stored on the accessory side as a `List<String>` of console IDs.

- The accessory knows which consoles it works with.
- The console does not need to know its accessories (avoids bidirectional coupling).
- In CSV persistence, IDs are stored as a `|`-separated string.

This design keeps the domain clean and simple.

---

### 2.4 Modifications in `SaleService` to include accessories

**Question:** What modifications are needed so sales include accessories without breaking existing behavior?

**Answer:**

- `registerSale` accepts `List<Product>` (accessories are `Product` subclasses).
- Stock validation delegates to `AccessoryService` when the item is an `Accessory`, and to `ProductService` otherwise.
- Inventory updates call `AccessoryService.updateStock` for accessories and `ProductService.reduceStock` for other products.
- No breaking changes to existing logic for video games and consoles.

---

### 2.5 Layer location of accessory classes

**Question:** In which layer should the new accessory classes be located?

**Answer:**

- `Accessory`, `Controller`, `Cable`, `Memory` → **model**
- `AccessoryRepository` → **persistence**
- `AccessoryService` → **service**
- Accessory submenu → **ui**

This respects the layered architecture `ui → service → persistence → model`.

---

## Section 3 — Promotion Module

### 3.1 How do the three promotions share behavior while differing?

**Question:** How is the promotion hierarchy designed so each type calculates its discount differently without the rest of the system knowing concrete types?

**Answer:**

Through an **abstract base class** `Promotion` with:

- Common attributes: `id`, `name`, `startDate`, `endDate`
- Concrete method: `isActive(LocalDate)`
- Abstract method: `calculateDiscount(Sale)`

Subclasses `PercentageDiscount`, `CategoryDiscount`, and `BulkPurchaseDiscount` each implement their own calculation. The rest of the system interacts only with `Promotion`, using **polymorphism**.

---

### 3.2 How is `calculateDiscount` declared?

**Question:** How is the discount calculation method declared in the base class?

**Answer:**

As an abstract method:

```java
public abstract double calculateDiscount(Sale sale);
```

This guarantees every subclass provides its own logic while the service layer treats all promotions uniformly.

---

### 3.3 Where is "best promotion" selection located?

**Question:** Where is the "best promotion" logic located and why?

**Answer:**

In `PromotionService.findBestPromotionFor(Sale)`. This is coherent with the layered architecture because:

- Business rules belong to the **service layer**, not to `Sale` (single responsibility).
- The UI must not contain business logic.
- `Sale` should not know about promotions; it only stores the applied result.

---

### 3.4 Modifications in `Sale` and `generateReceipt`

**Question:** What modifications are needed in `Sale` and `generateReceipt` to show the discount?

**Answer:**

- Add `appliedPromotionName: String` and `discountAmount: double`.
- Modify `generateReceipt()` to display subtotal, promotion name, discount amount, and final total.

These changes are **additive** and do not break existing behavior.

---

### 3.5 Where is "active promotion" validation performed?

**Question:** Where is the active-promotion validation located?

**Answer:**

In `Promotion.isActive(LocalDate)`, invoked from `PromotionService`. The domain rule lives in `Promotion` (the entity knows when it is valid), but the service decides when to apply it.

---

## Section 4 — Warranty Module

### 4.1 Hierarchy design

**Question:** How is the warranty hierarchy designed and what OOP mechanism allows each type to have its own duration without duplicating code?

**Answer:**

Through an **abstract base class** `Warranty` with:

- Common attributes: `id`, `product`, `sale`, `startDate`, `endDate`
- Abstract methods: `getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()`
- Concrete methods: `isActive(LocalDate)`, `generateWarrantyCertificate()`

The constructor calls `getDurationInMonths()` to compute `endDate`:

```java
this.endDate = startDate.plusMonths(getDurationInMonths());
```

**Polymorphism** ensures each subclass provides its own duration. No duplication.

- `BasicWarranty`: 6 months, cost 0
- `ExtendedWarranty`: 12 months, cost 10% of product price

---

### 4.2 Where is the "only consoles get basic warranty" rule?

**Question:** In which layer is this decision located and what Java mechanism is used?

**Answer:**

In `SaleService.registerSale`, using `instanceof Console`. This belongs to the **service layer** because it is a cross-module business rule (sales + warranties). The Java mechanism used is `instanceof` with pattern matching.

---

### 4.3 How is the expiration date calculated?

**Question:** How is `endDate` calculated in each subclass?

**Answer:**

Centrally in the `Warranty` constructor:

```java
this.endDate = startDate.plusMonths(getDurationInMonths());
```

Each subclass overrides `getDurationInMonths()`. This centralizes the calculation, avoids duplication, and ensures consistency.

---

### 4.4 Where is the extended warranty cost applied?

**Question:** Where is the extended warranty cost calculated and applied to the sale?

**Answer:**

In `SaleService.registerSale`, after generating warranties:

- The service sums `getAdditionalCost()` of each extended warranty.
- The total is stored in `sale.extendedWarrantyCost`.
- Final total: `subtotal − discount + extendedWarrantyCost`.

The `Sale.generateReceipt()` shows this cost separately.

---

### 4.5 Where is `listWarrantiesExpiringSoon` located?

**Question:** Where is this method located and what dependencies does it need?

**Answer:**

In `WarrantyService`. It iterates over all warranties and filters those whose `endDate` is within the next `daysAhead` days. This is coherent with the service layer because it involves date-based business rules and is used by the UI.

---

## Section 5 — Return Module

### 5.1 Relationship between `Return` and `Sale`

**Question:** What type of relationship exists between `Return` and `Sale`?

**Answer:**

A **unidirectional association**. `Return` holds a reference to `Sale`, but `Sale` does not know about its returns.

Justification:

- A return cannot exist without a sale (existence dependency).
- A sale can exist without returns.
- Composition would be too strong (deleting a sale must not delete historical returns).

---

### 5.2 Partial returns

**Question:** How is a partial return represented?

**Answer:**

In `Return.returnedProducts: List<Product>`, containing only the products actually returned, not necessarily all products from the original sale.

---

### 5.3 Where is the 30-day rule validated?

**Question:** Where is this validation located and what Java mechanism is used?

**Answer:**

In `Sale.canBeReturned()`, called from `ReturnService.registerReturn`. The method uses:

```java
date.plusDays(30) and isBefore()
```

`LocalDateTime.plusDays()` and `isBefore()` compute the difference. The rule is on the sale (property of the entity), but enforced at the **service layer**.

---

### 5.4 Which method is reused for stock restoration?

**Question:** Which existing method is reused and why is reusing important?

**Answer:**

- `ProductService.restoreStock(String, int)`
- `AccessoryService.restoreStock(String, int)`

`ReturnService` delegates based on the runtime type of the returned item. Reusing existing methods avoids duplicating stock logic and maintains a **single source of truth**.

---

### 5.5 Where is the monthly balance report located?

**Question:** Where is the monthly balance report located and what dependencies does it need?

**Answer:**

In `ReturnService`, with methods `calculateMonthlySales`, `calculateMonthlyReturns`, and `generateMonthlyBalance`. It consolidates data from two modules (sales and returns).

Dependencies:

- `SaleRepository` (or `SaleService`) for sales data.
- `ReturnRepository` for returns data.

The service layer is the correct location because the report crosses module boundaries and involves business rules.

---

## Section 6 — Integration Adjustments (A1–A7)

### A1 — Category discount for accessories

**Cause:** Original `CategoryDiscount` only accepted `VIDEOGAME` and `CONSOLE`.

**Solution:** `calculateDiscount` recognizes `Accessory` instances when category is `ACCESSORY`. `PromotionService.registerCategoryDiscount` validates against the three allowed values. A new accessory promotion was added to `data/promotions.csv`.

---

### A2 — Circular dependency in warranty module

**Cause:** `WarrantyRepository` depended on `SaleService`, creating the cycle:

```
SaleService → WarrantyService → WarrantyRepository → SaleService
```

**Solution:** `WarrantyRepository` persists only sale and product IDs. `WarrantyService` resolves references using `SaleRepository` and `ProductService` injected by constructor. `Main` builds the dependencies in the correct order.

---

### A3 — Unified sale registration flow

**Cause:** Three requirements modified `SaleService.registerSale` independently, and the order of operations affected the result.

**Solution:** `registerSale` executes in this order:

1. Validate at least one item.
2. Validate stock (products and accessories).
3. Create the sale and calculate subtotal.
4. Query `PromotionService.findBestPromotionFor(sale)` and register the discount.
5. Generate basic warranties for consoles and extended warranties when requested.
6. Calculate final total: `subtotal − discount + extendedWarrantyCost`.
7. Update inventory (delegating to `ProductService` or `AccessoryService`).
8. Persist the sale.

`Sale.generateReceipt` shows subtotal, promotion name, discount, extended warranty cost, and final total.

---

### A4 — Accessory return stock

**Cause:** `ReturnService` only called `ProductService.restoreStock`, which does not manage accessory inventory.

**Solution:** `ReturnService` injects `AccessoryService` and delegates stock restoration based on the runtime type of the returned item. `AccessoryService.restoreStock` was added.

---

### A5 — Refund for discounted sales

**Cause:** `Return.calculateRefundAmount` summed list prices, over-refunding when a promotion was applied.

**Solution:** The refund is now proportional to the original discount:

```
refund = price × (1 − discountAmount / subtotal)
```

`generateReturnReceipt` shows list price, proportional refund, and total per item.

---

### A6 — Monthly balance report

**Cause:** `generateMonthlyBalance` returned only the net balance.

**Solution:** Added `calculateMonthlySales` and `calculateMonthlyReturns`. `generateMonthlyBalance` returns the difference. The sales total uses the final total of each sale (with discount and extended warranty cost).

---

### A7 — Warranty cancellation on console return

**Cause:** No requirement defined what happens to a console's warranty when the console is returned.

**Solution:** `WarrantyService.cancelWarranties(productId, saleId)` removes all warranties of the product in the sale and returns the refundable amount (0 for basic, extended cost for extended). `ReturnService.registerReturn` calls this method and adds the refund to the total.

---

## Summary of Design Decisions

| Decision | Justification |
|----------|---------------|
| `Person`, `Product`, `Accessory`, `Promotion`, `Warranty` are abstract | Domain requires concrete types; promotes polymorphism |
| Layered architecture `ui → service → persistence → model` | Separation of concerns |
| CSV persistence with discriminators | Readable, editable, and matches the requirements |
| Business rules in services | Single source of truth; UI must not contain logic |
| Polymorphism for promotions and warranties | Avoids `if/else` chains and duplicated logic |
| Proportional refund on returns | Reflects what the customer actually paid |
| Warranty cancellation on return | Integrated and coherent system behavior |