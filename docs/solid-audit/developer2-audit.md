# SOLID Audit — Developer 2

**Auditor:** Developer 2  
**Date:** 2026-10-21  
**Commit audited:** `develop` at the start of Requirement 8  

## Scope

Classes audited by Developer 2:

- `Person`, `Customer`, `Seller`
- `PersonRepository`, `PersonService`
- `AccessoryRepository`, `AccessoryService`
- `PromotionRepository`, `PromotionService`
- `WarrantyRepository`, `WarrantyService`
- `ReturnRepository`, `ReturnService`
- Exception and validator usage in services and repositories
- Classes created or modified by Developer 2 during the R7 GRASP correction
  (see `docs/grasp-refactor-log.md`)

---

## 1. Single Responsibility Principle (SRP)

**Verdict:** Violation

### V-04 — `PersonService` seeds default sellers in the constructor

| Field | Content |
|-------|---------|
| **ID** | V-04 |
| **Principle** | SRP |
| **Module** | People (service) |
| **Evidence** | `PersonService` constructor: `if (this.sellers.isEmpty()) { sellers.add(new Seller(...)); ...; repository.saveSellers(this.sellers); }` |
| **Explanation** | Registration logic mixed with lifecycle management. |
| **Consequence** | Changing seed data requires editing the service. Unit-testing `PersonService` triggers filesystem writes on a fresh environment. |
| **Proposed solution** | Extract `SellerSeeder` (or `DataSeeder`) with a single method `seed(PersonRepository)`. `Main` calls it before constructing `PersonService`. |
| **Cross-integrante code** | No |

### V-06 — `ReturnService` mixes registration with monthly reporting

| Field | Content |
|-------|---------|
| **ID** | V-06 |
| **Principle** | SRP |
| **Module** | Returns (service) |
| **Evidence** | `registerReturn` (registration) + `calculateMonthlySales`, `calculateMonthlyReturns`, `generateMonthlyBalance` (reporting) |
| **Explanation** | Two independent reasons to change. |
| **Consequence** | Changing the monthly report format forces an edit to the class that handles returns. |
| **Proposed solution** | Extract `MonthlyBalanceService` that takes `ReturnRepository` + `SaleRepository`. `ReturnService` keeps only registration and queries. |
| **Cross-integrante code** | No |

---

## 2. Open/Closed Principle (OCP)

**Verdict:** Violation

### V-10b — `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` use `instanceof`

| Field | Content |
|-------|---------|
| **ID** | V-10b |
| **Principle** | OCP |
| **Module** | Accessories, Promotions, Warranties (persistence) |
| **Evidence** | All three repositories branch on `instanceof` in `toCsvLine` and switch on the string `type` in `fromCsvLine`. |
| **Explanation** | Adding a new subtype requires editing the repository. |
| **Consequence** | Persistence layer is not closed for modification. |
| **Proposed solution** | Coordinate with Developer 1 (V-10a): model classes add `toCsvFragment()` and `getTypeDiscriminator()`. These three repositories call the polymorphic methods instead of `instanceof`. |
| **Cross-integrante code** | Yes — Developer 1 owns the model classes |

### V-11 — `PromotionService.registerCategoryDiscount` hardcodes categories

| Field | Content |
|-------|---------|
| **ID** | V-11 |
| **Principle** | OCP |
| **Module** | Promotions (service) |
| **Evidence** | `if (!category.equalsIgnoreCase("VIDEOGAME") && !category.equalsIgnoreCase("CONSOLE") && !category.equalsIgnoreCase("ACCESSORY")) throw new IllegalArgumentException("Invalid category: " + category);` |
| **Explanation** | Adding a category requires editing the service. |
| **Consequence** | Low severity (three categories is stable), but a foreseeable change. |
| **Proposed solution** | Introduce `Category` enum with constants `VIDEOGAME`, `CONSOLE`, `ACCESSORY`. `PromotionService.registerCategoryDiscount(Category)` validates against `Category.values()`. `Product.getCategory()` returns `Category`, not `String`. |
| **Cross-integrante code** | No |

---

## 3. Liskov Substitution Principle (LSP)

**Verdict:** Correct for the classes I own, **violation via services** (border)

The classes I own that participate in the LSP violation are:

- `AccessoryService.findById` throws `NoSuchElementException` while
  `ProductService.findById` returns `null`. Same method name, different
  contract. This is **V-14** (ISP, reported by Developer 1), and both
  V-12 (LSP, reported by the Technical Lead) and V-14 converge on the same
  fix: the `InventoryService` façade.

**Verdict on my classes:** `Person`, `Customer`, `Seller`, `PersonRepository`,
`PersonService`, and the repositories I own do not violate LSP as classes.
The failure is at the service interface level (`findById` contract), which is
V-14 and belongs to Developer 1 + Developer 2.

---

## 4. Interface Segregation Principle (ISP)

**Verdict:** Violation (border with Developer 1)

### V-14 — Divergent `findById` contracts

| Field | Content |
|-------|---------|
| **ID** | V-14 |
| **Principle** | ISP |
| **Module** | Accessories (service) |
| **Evidence** | `AccessoryService.findById` throws `NoSuchElementException`. `ProductService.findById` returns `null`. |
| **Explanation** | Same method name, opposite error semantics. Clients (e.g., `SaleService.hasEnoughStockAccessory`) must know which one they are calling. |
| **Consequence** | A client that handles "not found" as `null` breaks when it receives an exception, and vice versa. |
| **Proposed solution** | Normalize via `InventoryService.findById(String): Optional<Product>`. Both underlying services delegate. |
| **Cross-integrante code** | Yes — Developer 1 owns `ProductService` |

---

## 5. Dependency Inversion Principle (DIP)

**Verdict:** Violation

### V-16 — `ReturnService` depends on 5 concrete classes

| Field | Content |
|-------|---------|
| **ID** | V-16 |
| **Principle** | DIP |
| **Module** | Returns (service) |
| **Evidence** | Constructor: `ReturnService(ReturnRepository, SaleRepository, ProductService, AccessoryService, WarrantyService)` |
| **Explanation** | Five concrete dependencies. No interface. |
| **Consequence** | Any change in any of them affects `ReturnService`. Unit-testing requires real CSV files. |
| **Proposed solution** | Introduce `InventoryService` (reduces 5 → 4) and interfaces (`IReturnRepository`, `ISaleRepository`, `IWarrantyService`). |
| **Cross-integrante code** | Yes — Technical Lead |

### V-17c — `AccessoryService`, `PromotionService`, `WarrantyService`, `PersonService` depend on concrete repositories

| Field | Content |
|-------|---------|
| **ID** | V-17c |
| **Principle** | DIP |
| **Module** | Accessories, Promotions, Warranties, People (services) |
| **Evidence** | All four constructors take the concrete repository. |
| **Explanation** | Same issue as V-17a (Developer 1). |
| **Consequence** | Unit-testing any of these services requires a real CSV file. |
| **Proposed solution** | Extract `IAccessoryRepository`, `IPromotionRepository`, `IWarrantyRepository`, `IPersonRepository`. |
| **Cross-integrante code** | No |

### V-18 — `WarrantyRepository` and `ReturnRepository` depend on 3 concrete repositories

| Field | Content |
|-------|---------|
| **ID** | V-18 |
| **Principle** | DIP |
| **Module** | Warranties, Returns (persistence) |
| **Evidence** | `WarrantyRepository(SaleRepository, ProductRepository, AccessoryRepository)` and `ReturnRepository(SaleRepository, ProductRepository, AccessoryRepository)` |
| **Explanation** | Three concrete repositories each. The A2 GRASP fix moved the dependency from `SaleService` (upward) to `SaleRepository` (lateral), which reduced but did not eliminate the DIP violation (see `solid-analysis.md` Q3, Case B1). |
| **Consequence** | Unit-testing requires real CSV files for all five repositories. |
| **Proposed solution** | Extract `ISaleLookup` and `IProductLookup` interfaces (both implemented by `SaleRepository` and `ProductRepository` respectively). Both repositories depend on the interfaces. |
| **Cross-integrante code** | Yes — Developer 1 owns `WarrantyRepository` side of the border |

---

## Summary of violations found by Developer 2

| ID | Principle | Class | Severity |
|----|-----------|-------|----------|
| V-04 | SRP | `PersonService` | Low |
| V-06 | SRP | `ReturnService` | Medium |
| V-10b | OCP | `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` | Medium (border) |
| V-11 | OCP | `PromotionService` | Low |
| V-14 | ISP | `ProductService` / `AccessoryService` | Medium (border) |
| V-16 | DIP | `ReturnService` | High |
| V-17c | DIP | `AccessoryService`, `PromotionService`, `WarrantyService`, `PersonService` | Medium |
| V-18 | DIP | `WarrantyRepository`, `ReturnRepository` | Medium (border) |