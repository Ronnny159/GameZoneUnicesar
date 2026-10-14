# Warranty Module Analysis — GameZone Unicesar

## Module overview

The warranty module formalizes the coverage offered with each console sale. Every console sold gets a free basic warranty of 6 months. The seller may optionally offer an extended warranty of 12 months for 10% of the product price.

## Analysis questions

### 1. How is the warranty hierarchy designed, and what OOP mechanism allows each type to have its own duration without duplicating code?

**Answer:** Through an **abstract base class** `Warranty` with:

- Common attributes: `id`, `product`, `sale`, `startDate`, `endDate`
- Abstract methods: `getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()`
- Concrete methods: `isActive(LocalDate)`, `generateWarrantyCertificate()`

The constructor calls `getDurationInMonths()` to compute `endDate`:

```java
this.endDate = startDate.plusMonths(getDurationInMonths());
```

**Polymorphism** ensures each subclass provides its own duration. No code is duplicated.

- `BasicWarranty`: 6 months, cost 0
- `ExtendedWarranty`: 12 months, cost 10% of product price

### 2. Where is the rule "only consoles get basic warranty" applied?

**Answer:** In `SaleService.registerSale`, using `instanceof Console`. This belongs to the **service layer** because it is a business rule involving multiple modules (sales and warranties).

### 3. How is the expiration date calculated?

**Answer:** Centrally in the `Warranty` constructor:

```java
this.endDate = startDate.plusMonths(getDurationInMonths());
```

Each subclass overrides `getDurationInMonths()`. This centralizes the calculation, avoids duplication, and guarantees consistency.

### 4. Where is the extended warranty cost applied to the sale?

**Answer:** In `SaleService.registerSale`, after generating warranties:

- The service sums `getAdditionalCost()` of each extended warranty.
- The total is stored in `sale.extendedWarrantyCost`.
- Final total: `subtotal − discount + extendedWarrantyCost`.

`Sale.generateReceipt()` shows this cost separately.

### 5. Where is `listWarrantiesExpiringSoon` located?

**Answer:** In `WarrantyService`. It iterates over all warranties and filters those whose `endDate` is within the next `daysAhead` days. This belongs to the service layer because it involves date-based business rules and is used by the UI.

## Classes introduced

| Class | Layer | Type | Purpose |
|-------|-------|------|---------|
| `Warranty` | model | abstract | Common base for all warranties |
| `BasicWarranty` | model | concrete | 6 months, no extra cost |
| `ExtendedWarranty` | model | concrete | 12 months, 10% of price |
| `WarrantyRepository` | persistence | concrete | CSV persistence with type discriminator |
| `WarrantyService` | service | concrete | Assignment, queries, cancellation |
| `WarrantyMenu` | ui | concrete | Console submenu for warranties |

## Files touched

- `SaleService.registerSale` — generates basic warranties for consoles and optional extended warranties (A3)
- `Sale` — stores `extendedWarrantyCost`
- `ReturnService.registerReturn` — cancels warranties on console returns (A7)
- `ConsoleMenu` — new warranty submenu
- `data/warranties.csv` — new file for persistence

## Related integration adjustments

- **A2:** `WarrantyRepository` no longer depends on `SaleService` (breaks the circular dependency). It persists only IDs and resolves them in `WarrantyService`.
- **A3:** `SaleService.registerSale` generates warranties after applying promotions and before updating inventory.
- **A7:** `WarrantyService.cancelWarranties` is invoked when a console is returned.