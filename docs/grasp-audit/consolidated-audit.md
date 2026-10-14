# Consolidated GRASP Audit — GameZone Unicesar

**Team:** GameZone Unicesar  
**Consolidated by:** Technical Lead  
**Date:** 2026-10-14  
**Commit audited:** `develop` (before Requirement 7)  
**Reference commit:** `[hash]`

---

## Coverage table

| Class | Audited by |
|-------|-----------|
| `Person`, `Customer`, `Seller` | Developer 2 |
| `PersonRepository`, `PersonService` | Developer 2 |
| `Product`, `VideoGame`, `Console` | Developer 1 |
| `Accessory`, `Controller`, `Cable`, `Memory` | Developer 1 |
| `AccessoryRepository`, `AccessoryService` | Developer 2 |
| `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 |
| `PromotionRepository`, `PromotionService` | Developer 2 |
| `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 |
| `WarrantyRepository`, `WarrantyService` | Developer 2 |
| `Return`, `Sale.canBeReturned` | Developer 1 |
| `ReturnRepository`, `ReturnService` | Developer 2 |
| `Sale`, `SaleRepository`, `SaleService` | Technical Lead |
| `ConsoleMenu`, `Main`, all UI menus | Technical Lead |
| `ProductService.restoreStock` | Technical Lead |

**Conclusion:** Every class was audited by its owner. No class was omitted or double-audited.

---

## Final verdict per pattern

| Pattern | Verdict |
|---------|---------|
| Experto en Información | Violation (V-05) |
| Creador | Violation (V-09) |
| Controlador | Violation (V-01) |
| Bajo Acoplamiento | Violation (V-04) |
| Alta Cohesión | Violation (V-03) |
| Polimorfismo | Violation (V-02) |
| Fabricación Pura | Correct |
| Indirección | Violation (V-07) |
| Variaciones Protegidas | Violation (V-08) |

---

## Definitive list of violations

### V-01 — Controlador violation in SaleMenu

| Field | Content |
|-------|---------|
| **Pattern** | Controlador |
| **Evidence** | `SaleMenu.registerSale`, `if (p instanceof Console)` at line ~93 |
| **Explanation** | UI decides which products get extended warranty |
| **Consequence** | Business rule change requires UI modification |
| **Solution** | Move decision to `SaleService` |
| **Origin** | leader-audit V-01 |
| **Responsible** | Technical Lead |

### V-02 — Polimorfismo violation in SaleService

| Field | Content |
|-------|---------|
| **Pattern** | Polimorfismo |
| **Evidence** | `SaleService.registerSale`, `if (p instanceof Accessory)` and `if (p instanceof Console)` |
| **Explanation** | Type-based branching |
| **Consequence** | New product type requires service modification |
| **Solution** | Add `Product.requiresBasicWarranty()`, override in `Console`. Add `Product.isAccessory()` |
| **Origin** | leader-audit V-02 |
| **Responsible** | Technical Lead (service) + Developer 1 (Product hierarchy) |

### V-03 — Alta Cohesión violation in Sale

| Field | Content |
|-------|---------|
| **Pattern** | Alta Cohesión |
| **Evidence** | `Sale.generateReceipt()` |
| **Explanation** | Domain class holds presentation logic |
| **Consequence** | Receipt format change affects domain |
| **Solution** | Extract `ReceiptGenerator` in service layer |
| **Origin** | leader-audit V-03, developer1-audit V-03 |
| **Responsible** | Technical Lead |

### V-04 — Bajo Acoplamiento violation in SaleService

| Field | Content |
|-------|---------|
| **Pattern** | Bajo Acoplamiento |
| **Evidence** | `SaleService` constructor takes 6 dependencies |
| **Explanation** | High coupling |
| **Consequence** | Any service change affects SaleService |
| **Solution** | Introduce `InventoryService` wrapping `ProductService` + `AccessoryService` |
| **Origin** | leader-audit V-04, developer2-audit V-04 |
| **Responsible** | Technical Lead (SaleService) + Developer 2 (services) |
| **Border violation** | Yes |

### V-05 — Experto en Información violation in SaleService

| Field | Content |
|-------|---------|
| **Pattern** | Experto en Información |
| **Evidence** | `SaleService.registerSale` sums warranty cost |
| **Explanation** | Service computes what the warranty knows |
| **Consequence** | Warranty cost change affects service |
| **Solution** | Add `Sale.calculateWarrantyCost()` |
| **Origin** | leader-audit V-05, developer2-audit V-05 |
| **Responsible** | Technical Lead |
| **Border violation** | Yes |

### V-07 — Indirección violation in ReturnService

| Field | Content |
|-------|---------|
| **Pattern** | Indirección |
| **Evidence** | `ReturnService.registerReturn` calls different services based on type |
| **Explanation** | No intermediary |
| **Consequence** | New type requires service modification |
| **Solution** | Introduce `InventoryService` (same as V-04) |
| **Origin** | leader-audit V-07, developer2-audit V-07 |
| **Responsible** | Technical Lead + Developer 2 |
| **Border violation** | Yes |

### V-08 — Variaciones Protegidas violation in PromotionService

| Field | Content |
|-------|---------|
| **Pattern** | Variaciones Protegidas |
| **Evidence** | `PromotionService.registerCategoryDiscount` hardcodes categories |
| **Explanation** | Adding a category requires modifying the service |
| **Consequence** | Low — but foreseeable change |
| **Solution** | Introduce a `Category` enum |
| **Origin** | leader-audit V-08, developer2-audit V-08 |
| **Responsible** | Developer 2 |

### V-09 — Creador violation in PersonService

| Field | Content |
|-------|---------|
| **Pattern** | Creador |
| **Evidence** | `PersonService` constructor hardcodes seller creation |
| **Explanation** | Instantiation logic in constructor |
| **Consequence** | Low |
| **Solution** | Extract `SellerFactory` |
| **Origin** | developer2-audit V-09 |
| **Responsible** | Developer 2 |

---

## Border violations

### V-04 and V-07 — `InventoryService`

**Integrantes involved:** Technical Lead (`SaleService`) and Developer 2 (`ProductService`, `AccessoryService`)

**Registration:** Single violation V-04/V-07 (shared solution).

**Correction split:**
- Developer 2: creates `InventoryService` with `restoreStock(String id, int qty)` and `hasEnoughStock(String id, int qty)`
- Technical Lead: updates `SaleService` and `ReturnService` to use `InventoryService`

**Order:** Developer 2 first, then Technical Lead.

### V-05 — `Sale.calculateWarrantyCost`

**Integrantes involved:** Technical Lead (`Sale`, `SaleService`) and Developer 2 (`WarrantyService`)

**Registration:** Single violation V-05.

**Correction split:**
- Technical Lead: adds `Sale.calculateWarrantyCost()` and refactors `SaleService`
- Developer 2: no change needed (the warranty class already exposes `getAdditionalCost()`)

**Order:** Technical Lead only.

---

## Out-of-scope findings (functional defects)

| ID | Description | Evidence |
|----|-------------|----------|
| OF-01 | `ReturnRepository.fromCsvLine` returns `null` if sale is not found — this is correct, but the CSV writer never removes orphaned returns | `ReturnRepository.loadAll` silently skips them |
| OF-02 | `Sale.canBeReturned` uses `LocalDateTime.now()` but the sale's date is loaded from CSV as `LocalDateTime` — timezone is implicit | `Sale.canBeReturned` |

These defects are not corrected in Requirement 7 because they would change the observable behavior.

---

## Approval

| Integrante | Reviewed | Date |
|------------|----------|------|
| Technical Lead | Yes | 2026-10-14 |
| Developer 1 | Yes | 2026-10-14 |
| Developer 2 | Yes | 2026-10-14 |