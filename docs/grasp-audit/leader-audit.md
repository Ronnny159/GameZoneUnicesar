# GRASP Audit — Technical Lead

**Auditor:** Technical Lead  
**Date:** 2026-10-14  
**Commit audited:** `develop` (before Requirement 7)

## Scope

Classes audited by the Technical Lead:
- `Sale`, `SaleRepository`, `SaleService`
- `ConsoleMenu`, `ConsoleUtils`, `Main`
- All UI menus: `PersonMenu`, `ProductMenu`, `AccessoryMenu`, `PromotionMenu`, `SaleMenu`, `WarrantyMenu`, `ReturnMenu`
- `ProductService.restoreStock`

---

## 1. Experto en Información

**Verdict:** Violation

### V-05 — SaleService calculates warranty cost

| Field | Content |
|-------|---------|
| **Pattern** | Experto en Información |
| **Module** | Warranty integration |
| **Evidence** | `SaleService.registerSale`, lines ~78-84: `warrantyCost += ew.getAdditionalCost();` |
| **Explanation** | `SaleService` sums the warranty cost by iterating and calling `getAdditionalCost()`. The expert in this information is `Warranty` itself, which already knows its own cost. |
| **Consequence** | If the warranty cost formula changes (e.g., different percentage per product type), `SaleService` must be modified. |
| **Proposed solution** | Move the calculation into `Sale` (which knows all warranties) or introduce a method `Sale.calculateWarrantyCost()`. |
| **Cross-integrante code** | No |

---

## 2. Creador

**Verdict:** Correct

**Where:** `Main` creates all repositories and services via constructor injection. `PersonService.registerCustomer` creates `Customer` instances (the service holds the list, so it is the Creator). `SaleService.registerSale` creates `Sale` instances (it has the sale data and uses it).

---

## 3. Controlador

**Verdict:** Violation

### V-01 — SaleMenu decides on extended warranty

| Field | Content |
|-------|---------|
| **Pattern** | Controlador |
| **Module** | Sales UI |
| **Evidence** | `SaleMenu.registerSale`, lines ~93-100: iterates products and asks "Add extended warranty for console..." using `p instanceof Console` |
| **Explanation** | The UI decides which products require extended warranty. The Controlador should only receive input and delegate to the service. |
| **Consequence** | Any change to warranty business rules (e.g., extend to accessories) requires modifying `SaleMenu`. |
| **Proposed solution** | Move the decision to `SaleService`, which returns the list of warranties to apply. The UI only asks "yes/no" if the service says the product is eligible. |
| **Cross-integrante code** | No |

---

## 4. Bajo Acoplamiento

**Verdict:** Violation

### V-04 — SaleService depends on 6 collaborators

| Field | Content |
|-------|---------|
| **Pattern** | Bajo Acoplamiento |
| **Module** | Sales integration |
| **Evidence** | `SaleService` constructor takes `SaleRepository`, `ProductService`, `PersonService`, `AccessoryService`, `PromotionService`, `WarrantyService` |
| **Explanation** | 6 dependencies is high coupling. Any change in any of these services affects `SaleService`. |
| **Consequence** | Adding a new product type (e.g., `Headset`) requires modifying `SaleService`. |
| **Proposed solution** | Introduce `InventoryService` (Fabricación Pura) that wraps `ProductService` and `AccessoryService`. `SaleService` then depends on `InventoryService` instead of both. |
| **Cross-integrante code** | Yes — Developer 2 owns `ProductService` and `AccessoryService` |

---

## 5. Alta Cohesión

**Verdict:** Violation

### V-03 — Sale mixes domain and presentation

| Field | Content |
|-------|---------|
| **Pattern** | Alta Cohesión |
| **Module** | Sales model |
| **Evidence** | `Sale.generateReceipt()` builds a formatted string with all receipt details |
| **Explanation** | `Sale` should hold domain state. Formatting a receipt is presentation concern. |
| **Consequence** | Changing the receipt format requires modifying the domain class. |
| **Proposed solution** | Extract `ReceiptGenerator` (Fabricación Pura) in the service layer. |
| **Cross-integrante code** | No |

---

## 6. Polimorfismo

**Verdict:** Violation

### V-02 — SaleService uses instanceof

| Field | Content |
|-------|---------|
| **Pattern** | Polimorfismo |
| **Module** | Sales + Warranties integration |
| **Evidence** | `SaleService.registerSale`, lines ~45-55: `if (p instanceof Accessory)` and `if (p instanceof Console)` |
| **Explanation** | The service branches on concrete types instead of using polymorphic methods. |
| **Consequence** | Adding a new product type requires modifying `SaleService`. |
| **Proposed solution** | Add `Product.requiresBasicWarranty()` returning `false` by default, overridden in `Console` to return `true`. Add `Product.isAccessory()` or similar. |
| **Cross-integrante code** | No |

---

## 7. Fabricación Pura

**Verdict:** Violation

### V-06 — ConsoleMenu constructs services

| Field | Content |
|-------|---------|
| **Pattern** | Fabricación Pura |
| **Module** | UI |
| **Evidence** | `Main` creates all services and passes them to `ConsoleMenu`. `ConsoleMenu` then creates submenus. |
| **Explanation** | Actually this is correct. Let me re-check... |
| **Consequence** | No violation |
| **Proposed solution** | N/A |
| **Cross-integrante code** | No |

**Correction:** After review, `Main` and `ConsoleMenu` follow the pattern correctly. **Verdict:** Correct.

---

## 8. Indirección

**Verdict:** Violation

### V-07 — ReturnService duplicates stock restoration logic

| Field | Content |
|-------|---------|
| **Pattern** | Indirección |
| **Module** | Returns |
| **Evidence** | `ReturnService.registerReturn`, lines ~72-76: iterates products and calls `accessoryService.restoreStock` or `productService.restoreStock` based on type |
| **Explanation** | The UI/service decides which service to call. There is no intermediary that abstracts "restore stock for any product". |
| **Consequence** | Adding a new product type requires modifying `ReturnService`. |
| **Proposed solution** | Introduce `InventoryService` (shared with V-04) with a single method `restoreStock(String id, int qty)` that resolves the type internally. |
| **Cross-integrante code** | Yes — Developer 2 owns `AccessoryService` |

---

## 9. Variaciones Protegidas

**Verdict:** Violation

### V-08 — PromotionService hardcodes categories

| Field | Content |
|-------|---------|
| **Pattern** | Variaciones Protegidas |
| **Module** | Promotions |
| **Evidence** | `PromotionService.registerCategoryDiscount`, lines ~32-38: `if (!category.equalsIgnoreCase("VIDEOGAME") && !category.equalsIgnoreCase("CONSOLE") && !category.equalsIgnoreCase("ACCESSORY"))` |
| **Explanation** | If a new category is added, this validation must change. |
| **Consequence** | Adding a new category requires modifying `PromotionService`. |
| **Proposed solution** | Introduce an enum or a `Category` class that enumerates valid categories. |
| **Cross-integrante code** | No |

---

## Summary of violations found by the Technical Lead

| ID | Pattern | Class | Severity |
|----|---------|-------|----------|
| V-01 | Controlador | `SaleMenu` | High |
| V-02 | Polimorfismo | `SaleService` | High |
| V-03 | Alta Cohesión | `Sale` | Medium |
| V-04 | Bajo Acoplamiento | `SaleService` | High |
| V-05 | Experto en Información | `SaleService` | Medium |
| V-07 | Indirección | `ReturnService` | Medium |
| V-08 | Variaciones Protegidas | `PromotionService` | Low |