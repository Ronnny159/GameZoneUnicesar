# Promotion Module Analysis — GameZone Unicesar

## Module overview

The promotion module allows the store to launch discount campaigns of different types. When a sale is registered, the system queries all active promotions and applies the one with the highest monetary discount.

## Analysis questions

### 1. How do the three promotions share behavior while differing in their calculations?

**Answer:** Through an **abstract base class** `Promotion` that defines common attributes (`id`, `name`, `startDate`, `endDate`) and an abstract method `calculateDiscount(Sale)`. Subclasses `PercentageDiscount`, `CategoryDiscount`, and `BulkPurchaseDiscount` each implement their own calculation.

The mechanism used is **polymorphism**: the service layer interacts only with `Promotion`, and each subclass defines its own behavior without the rest of the system knowing the concrete type.

### 2. How is the discount calculation method declared in the base class?

**Answer:** As an abstract method:

```java
public abstract double calculateDiscount(Sale sale);
```

This guarantees every subclass provides its own logic.

### 3. Where is the "best promotion" selection located?

**Answer:** In `PromotionService.findBestPromotionFor(Sale)`. This is coherent with the layered architecture because:

- Business rules belong to the service layer.
- `Sale` should not know about promotions (single responsibility).
- The UI must not contain business logic.

### 4. What modifications are needed in `Sale` and `generateReceipt`?

**Answer:**

- Add `appliedPromotionName: String` and `discountAmount: double` attributes.
- Modify `generateReceipt()` to show the subtotal, promotion name, discount, and total.

These changes are **additive** and do not break existing behavior.

### 5. Where is the "active promotion" validation performed?

**Answer:** In `Promotion.isActive(LocalDate)`, invoked from `PromotionService`. The rule lives in the entity (each promotion knows when it is valid), but the service decides when to apply it.

## Classes introduced

| Class | Layer | Type | Purpose |
|-------|-------|------|---------|
| `Promotion` | model | abstract | Common base for all promotions |
| `PercentageDiscount` | model | concrete | Discount on total sale |
| `CategoryDiscount` | model | concrete | Discount on a specific category |
| `BulkPurchaseDiscount` | model | concrete | Discount by quantity |
| `PromotionRepository` | persistence | concrete | CSV persistence with type discriminator |
| `PromotionService` | service | concrete | Registration, listing, best promotion selection |
| `PromotionMenu` | ui | concrete | Console submenu for promotions |

## Files touched

- `Sale` — adds `appliedPromotionName` and `discountAmount`
- `Sale.generateReceipt()` — shows the discount breakdown
- `SaleService.registerSale` — applies the best promotion automatically (A3)
- `ConsoleMenu` — new promotion submenu
- `data/promotions.csv` — includes at least one promotion of each type (A1 adds accessory category)

## Related integration adjustments

- **A1:** `CategoryDiscount` supports the `ACCESSORY` category.
- **A3:** `SaleService.registerSale` queries `findBestPromotionFor` after computing the subtotal.