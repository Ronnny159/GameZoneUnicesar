# SOLID Audit — Technical Lead

**Auditor:** Technical Lead  
**Date:** 2026-10-21  
**Commit audited:** `develop` at the start of Requirement 8  

## Scope

Classes audited by the Technical Lead:

- `Sale`, `SaleRepository`, `SaleService`
- `ConsoleMenu`, `ConsoleUtils`, `Main`
- All UI menus: `PersonMenu`, `ProductMenu`, `AccessoryMenu`, `PromotionMenu`, `SaleMenu`, `WarrantyMenu`, `ReturnMenu`
- `ProductService.restoreStock` (single method, per R8 §4.1)
- Classes created or modified by the Technical Lead during the R7 GRASP
  correction (see `docs/grasp-refactor-log.md`)

---

## 1. Single Responsibility Principle (SRP)

**Verdict:** Violation

### V-01 — `Sale` mixes domain state with presentation

| Field | Content |
|-------|---------|
| **ID** | V-01 |
| **Principle** | SRP |
| **Module** | Sales (model) |
| **Evidence** | `Sale.generateReceipt()` builds a `StringBuilder` with the receipt layout (id, date, customer, seller, product lines, subtotal, promotion, discount, extended warranty cost, final total) |
| **Explanation** | `Sale` should hold sale state and totals. Receipt formatting is presentation. The method was extended in R7 (GRASP-A3) and now holds even more presentation logic. |
| **Consequence** | A change in receipt format (English, CSV, HTML) forces an edit of the domain entity. |
| **Proposed solution** | Extract `SaleReceiptFormatter` in a `com.gameZone.presentation` package. `Sale` exposes getters only. |
| **Cross-integrante code** | No |

### V-05 — `SaleService` accumulates five responsibilities

| Field | Content |
|-------|---------|
| **ID** | V-05 |
| **Principle** | SRP |
| **Module** | Sales (service) |
| **Evidence** | `SaleService.registerSale` performs: (1) stock validation, (2) sale creation, (3) best promotion lookup, (4) warranty assignment, (5) inventory update, (6) persistence |
| **Explanation** | Single method has five reasons to change. |
| **Consequence** | Any change in promotion rules, warranty rules, or inventory rules forces an edit of `registerSale`. |
| **Proposed solution** | Introduce `InventoryService` (see V-15) to absorb the inventory responsibility. The remaining orchestration is a legitimate coordinator role, but the stock validation and inventory update must be delegated. |
| **Cross-integrante code** | Yes — Developer 2 owns `ProductService` and `AccessoryService` (both absorbed into `InventoryService`) |

### V-07 — `SaleMenu` decides which consoles get extended warranty

| Field | Content |
|-------|---------|
| **ID** | V-07 |
| **Principle** | SRP |
| **Module** | Sales (UI) |
| **Evidence** | `SaleMenu.registerSale` iterates products, uses `p instanceof Console`, prompts "Add extended warranty for console ...", builds the `extendedWarrantyIds` list |
| **Explanation** | UI is making a business decision. UI should only display and read input. |
| **Consequence** | Making extended warranty available for accessories requires editing the UI. |
| **Proposed solution** | `SaleService.getExtendedWarrantyCandidates(List<Product>)` returns eligible products. UI only asks y/n on the service-provided list. |
| **Cross-integrante code** | No |

---

## 2. Open/Closed Principle (OCP)

**Verdict:** Violation

### V-08 — `SaleService.registerSale` uses `instanceof`

| Field | Content |
|-------|---------|
| **ID** | V-08 |
| **Principle** | OCP |
| **Module** | Sales (service) |
| **Evidence** | `if (p instanceof Accessory)` (stock validation, inventory update); `if (p instanceof Console)` (warranty assignment) |
| **Explanation** | Adding a new product type requires modifying `SaleService`. |
| **Consequence** | A new type requires editing a stable service. |
| **Proposed solution** | Add polymorphic `Product.requiresBasicWarranty()` (default `false`, overridden in `Console`) and route all stock operations through `InventoryService` (V-15). |
| **Cross-integrante code** | No |

### V-09 — `SaleMenu.registerSale` uses `instanceof Console`

| Field | Content |
|-------|---------|
| **ID** | V-09 |
| **Principle** | OCP |
| **Module** | Sales (UI) |
| **Evidence** | `SaleMenu.registerSale`: `if (p instanceof Console)` before prompting for extended warranty |
| **Explanation** | Same root cause as V-07. Listed separately because the fix path differs (UI delegation vs. service method extraction). |
| **Consequence** | Adding a new console subtype (e.g., `Handheld`) requires editing the UI. |
| **Proposed solution** | The `SaleService.getExtendedWarrantyCandidates` method from V-07 removes the `instanceof` from the UI. |
| **Cross-integrante code** | No |

---

## 3. Liskov Substitution Principle (LSP)

**Verdict:** Violation (border with Developer 2)

### V-12 — `Accessory` is a `Product` on paper but not through `ProductService`

| Field | Content |
|-------|---------|
| **ID** | V-12 |
| **Principle** | LSP |
| **Module** | Cross-module (model + persistence) |
| **Evidence** | `Accessory extends Product`. But `ProductService` loads from `products.csv` and `AccessoryService` loads from `accessories.csv`. `productService.reduceStock("ACC-001", 1)` silently returns `false` because `findById` returns `null` for accessories. `ProductRepository.toCsvLine` silently drops accessories. |
| **Explanation** | `Accessory` is not substitutable where a `Product` is expected by `ProductService` or `ProductRepository`. |
| **Consequence** | Code that treats `Accessory` polymorphically (e.g., `SaleService`) is forced to branch on `instanceof Accessory` (which is exactly V-08). |
| **Proposed solution** | Introduce `InventoryService` (see V-15) with a single `findById(String): Optional<Product>` that resolves both stores. Callers never branch. |
| **Cross-integrante code** | Yes — Developer 2 owns `ProductService` and `AccessoryService` |

---

## 4. Interface Segregation Principle (ISP)

**Verdict:** Violation (border with Developer 2)

### V-13 — `SaleService` and `ReturnService` depend on two parallel stock services

| Field | Content |
|-------|---------|
| **ID** | V-13 |
| **Principle** | ISP |
| **Module** | Sales + Returns (service) |
| **Evidence** | Both services inject `ProductService` **and** `AccessoryService`. Both only need `findById`, `hasEnoughStock`, `reduceStock`, `restoreStock`. The two services have divergent contracts (`findById` returns `null` in one, throws in the other). |
| **Explanation** | Clients depend on a fat interface split across two classes with inconsistent semantics. |
| **Consequence** | A single "restore stock for a product" operation forces clients to branch on type. |
| **Proposed solution** | Same `InventoryService` façade as V-12 and V-15. |
| **Cross-integrante code** | Yes — Developer 2 |

---

## 5. Dependency Inversion Principle (DIP)

**Verdict:** Violation

### V-15 — `SaleService` depends on 6 concrete classes

| Field | Content |
|-------|---------|
| **ID** | V-15 |
| **Principle** | DIP |
| **Module** | Sales (service) |
| **Evidence** | Constructor: `SaleService(SaleRepository, ProductService, PersonService, AccessoryService, PromotionService, WarrantyService)` |
| **Explanation** | Six concrete dependencies. No interface in the constructor. |
| **Consequence** | Any change in any of these services affects `SaleService`. Unit testing requires six concrete collaborators, each of which writes to CSV on construction. |
| **Proposed solution** | Minimum: introduce `InventoryService` (reduces 6 → 5). Preferred: extract `ISaleRepository`, `IProductInventory`, `IPersonService`, `IPromotionService`, `IWarrantyService`. |
| **Cross-integrante code** | Yes — Developer 2 |

---

## Summary of violations found by the Technical Lead

| ID | Principle | Class | Severity |
|----|-----------|-------|----------|
| V-01 | SRP | `Sale` | Medium |
| V-05 | SRP | `SaleService` | High |
| V-07 | SRP | `SaleMenu` | High |
| V-08 | OCP | `SaleService` | High |
| V-09 | OCP | `SaleMenu` | Medium |
| V-12 | LSP | `Accessory` / `ProductService` | High (border) |
| V-13 | ISP | `SaleService`, `ReturnService` | High (border) |
| V-15 | DIP | `SaleService` | High |