# Return Module Analysis — GameZone Unicesar

## Module overview

The return module allows customers to return one or more products from a previous sale within 30 days. The system validates the sale, updates inventory, cancels warranties when applicable, and generates a receipt with a proportional refund.

## Analysis questions

### 1. What type of relationship exists between `Return` and `Sale`?

**Answer:** A **unidirectional association**. `Return` holds a reference to `Sale`, but `Sale` does not know about its returns.

Justification:

- A return cannot exist without a sale (existence dependency).
- A sale can exist without returns.
- Composition would be too strong (deleting a sale must not delete historical returns).

### 2. How is a partial return represented?

**Answer:** In `Return.returnedProducts: List<Product>`, which contains only the products actually returned, not necessarily all products from the original sale.

### 3. Where is the 30-day rule validated?

**Answer:** In `Sale.canBeReturned()`, called from `ReturnService.registerReturn`. The method uses:

```java
date.plusDays(30) and isBefore()
```

`LocalDateTime.plusDays()` and `isBefore()` compute the difference. The rule lives in the sale (property of the entity), but is enforced at the **service layer**.

### 4. Which method is reused for stock restoration?

**Answer:**

- `ProductService.restoreStock(String, int)`
- `AccessoryService.restoreStock(String, int)`

`ReturnService` delegates based on the runtime type of the returned item. Reusing existing methods avoids duplicating stock logic and maintains a single source of truth.

### 5. Where is the monthly balance report located?

**Answer:** In `ReturnService`, with methods `calculateMonthlySales`, `calculateMonthlyReturns`, and `generateMonthlyBalance`. It consolidates data from two modules (sales and returns).

Dependencies:

- `SaleRepository` (or `SaleService`) for sales data.
- `ReturnRepository` for returns data.

The service layer is the correct location because the report crosses module boundaries and involves business rules.

## Classes introduced

| Class | Layer | Type | Purpose |
|-------|-------|------|---------|
| `Return` | model | concrete | Represents a return of products from a sale |
| `ReturnRepository` | persistence | concrete | CSV persistence with reference resolution |
| `ReturnService` | service | concrete | Registration, queries, monthly balance |
| `ReturnMenu` | ui | concrete | Console submenu for returns and monthly balance |

## Files touched

- `Sale` — adds `canBeReturned()` (additive)
- `ProductService` — adds `restoreStock(String, int)` (additive)
- `AccessoryService` — adds `restoreStock(String, int)` (A4)
- `WarrantyService` — adds `cancelWarranties(String, String)` (A7)
- `ConsoleMenu` — new return submenu and monthly balance option
- `data/returns.csv` — new file for persistence

## Related integration adjustments

- **A4:** `ReturnService` injects `AccessoryService` to restore accessory stock.
- **A5:** `Return.calculateRefundAmount` applies the discount proportionally.
- **A6:** `ReturnService` provides `calculateMonthlySales`, `calculateMonthlyReturns`, and `generateMonthlyBalance`.
- **A7:** `WarrantyService.cancelWarranties` is called for each returned console.