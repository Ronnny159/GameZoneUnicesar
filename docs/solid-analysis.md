# SOLID Analysis — GameZone Unicesuar

**Team:** GameZone Unicesuar  
**Course:** Programming III  
**Program:** Systems Engineering — Universidad Popular del Cesar  
**Instructor:** Ing. Esp. Alfredo Bautista  
**Commit audited:** `develop` at the start of Requirement 8  

---

## 1. Which classes have more than one reason to change? (SRP)

**Answer:**

Seven classes have more than one reason to change. The two recurring "second
reasons" are presentation formatting and cross-cutting orchestration.

| Class | First responsibility (domain) | Second reason to change |
|-------|-------------------------------|--------------------------|
| `Sale` | Holds sale state (customer, seller, products, discount, extended warranty cost) | Builds the receipt string (`generateReceipt`) |
| `Return` | Holds return state | Builds the return receipt string (`generateReturnReceipt`) |
| `Warranty` | Holds warranty state (`startDate`, `endDate`, `product`, `sale`) | Builds the warranty certificate string (`generateWarrantyCertificate`) |
| `PersonService` | Customer registration and read access | Seeds the three default sellers inside the constructor |
| `ReturnService` | Registers returns | Calculates the monthly balance report |
| `SaleService` | Registers sales | Also validates stock, applies the best promotion, assigns warranties, updates inventory, and persists — five responsibilities in a single method |
| `SaleMenu` | Displays UI and receives input | Decides whether a console gets an extended warranty |

**Consequence:** A change in receipt format, report format, seeding policy, or
warranty eligibility forces edits in classes that are supposed to be stable.
The A3 GRASP fix from R7 actually **widened** `Sale.generateReceipt` (added
subtotal, promotion name, discount, extended warranty cost, final total) and
therefore made the SRP violation of `Sale` worse.

---

## 2. Which parts of the code must be modified when a new type is added? (OCP)

**Answer:**

Eight code sites branch on concrete types via `instanceof` or hardcoded sets.
Adding a new sellable type (`Headset`, `Figurine`) or a new category forces
modifications in each of them.

| Site | Branching mechanism |
|------|---------------------|
| `SaleService.registerSale` | `p instanceof Accessory` (stock + inventory), `p instanceof Console` (warranty) |
| `ReturnService.registerReturn` | `p instanceof Accessory` (stock restore), `p instanceof Console` (warranty cancellation) |
| `SaleMenu.registerSale` | `p instanceof Console` (extended-warranty prompt) |
| `AccessoryMenu.selectCompatibleConsoles` | `p instanceof Console` (console filter) |
| `ProductRepository.toCsvLine` / `fromCsvLine` | `instanceof VideoGame` / `instanceof Console` + string `type` switch |
| `AccessoryRepository.toCsvLine` / `fromCsvLine` | `instanceof Controller` / `Cable` / `Memory` + string `type` switch |
| `PromotionRepository.toCsvLine` / `fromCsvLine` | `instanceof PercentageDiscount` / `CategoryDiscount` / `BulkPurchaseDiscount` + string `type` switch |
| `WarrantyRepository.toCsvLine` / `fromCsvLine` | `warranty instanceof BasicWarranty` + string `type` switch |
| `PromotionService.registerCategoryDiscount` | Hardcoded `"VIDEOGAME"`, `"CONSOLE"`, `"ACCESSORY"` |

**Consequence:** Violates "open for extension, closed for modification". A new
accessory type (`Headset`) requires touching `AccessoryRepository`,
`SaleService`, `ReturnService`, and `AccessoryMenu`. A new category requires
touching `PromotionService`.

The GRASP-A1 fix from R7 closed the **model-side** OCP violation
(`CategoryDiscount.calculateDiscount` now uses `product.getCategory()`), but
the **service-side** validation in `PromotionService.registerCategoryDiscount`
was not touched and still hardcodes the three categories.

---

## 3. How do the SOLID findings relate to the GRASP audit from R7?

**Answer:**

The two audits are complementary but address different questions. GRASP
audits **who has the responsibility**; SOLID audits **how responsibilities are
structured so the code stays maintainable**. In this codebase, three concrete
relationships appear, covering the two required cases.

### Case A — A SOLID violation survived a GRASP fix

**A1. `Sale.generateReceipt()` (SRP violation that the GRASP-A3 fix worsened)**

- **GRASP R7:** never flagged. The method is cohesive in the narrow sense: it
  builds one string.
- **GRASP A3 (R7):** the method was **extended** to show subtotal, promotion
  name, discount, extended warranty cost, and final total. A3 focused on the
  orchestration order inside `SaleService.registerSale`, not on where the
  string is built.
- **SOLID R8 (V-01, SRP):** `Sale` now holds domain state *and* presentation
  logic. A3 made V-01 worse, not better, because the presentation block grew.
- **Lesson:** GRASP does not catch SRP. The A3 fix was correct for GRASP but
  aggravated the SOLID violation.

**A2. `PromotionService.registerCategoryDiscount` (OCP violation that survived)**

- **GRASP A1 (R7):** fixed `CategoryDiscount.calculateDiscount` to use
  `product.getCategory()` instead of `instanceof`. This was a **model-side**
  Variaciones Protegidas fix.
- **SOLID R8 (V-11, OCP):** the **service-side** validation still hardcodes
  `"VIDEOGAME"`, `"CONSOLE"`, `"ACCESSORY"`. The GRASP fix did not reach the
  service.
- **Lesson:** GRASP fixes scoped to one layer do not automatically propagate
  to adjacent layers.

### Case B — A GRASP correction incidentally fixed a SOLID violation

**B1. `WarrantyRepository` dependency graph (DIP violation partially fixed)**

- **Before R7:** `WarrantyRepository` (persistence) depended on `SaleService`
  (service). This is the worst possible DIP violation — an upward dependency
  across layers.
- **GRASP A2 (R7):** fixed the circular dependency
  `SaleService → WarrantyService → WarrantyRepository → SaleService` by
  making `WarrantyRepository` depend on `SaleRepository` + `ProductRepository`
  instead of `SaleService`.
- **SOLID R8 (V-18, partial DIP):** after A2, `WarrantyRepository` depends on
  same-layer repositories. The violation is not fully solved (concrete classes
  remain), but the severity was reduced from "upward dependency" to
  "lateral dependency". A2 incidentally improved DIP.

**B2. `CategoryDiscount.calculateDiscount` (model-side OCP fixed)**

- **Before R7:** `instanceof` on `VideoGame` / `Console` / `Accessory`.
- **GRASP A1 (R7):** replaced `instanceof` with `product.getCategory()`.
- **SOLID R8:** this also closed the **model-side** OCP violation in
  `CategoryDiscount`. Any new `Product` subtype implementing `getCategory()`
  works without touching `CategoryDiscount`.

### Summary table

| SOLID finding | Status before R7 | GRASP R7 effect | Status after R7 | Relationship |
|---------------|------------------|-----------------|-----------------|--------------|
| V-01 (`Sale.generateReceipt`) | SRP violation | A3 *extended* the method | SRP violation (worse) | Case A |
| V-11 (`PromotionService.registerCategoryDiscount`) | OCP violation | A1 fixed only the model side | OCP violation (service side remains) | Case A |
| V-18 (`WarrantyRepository` dependencies) | Worst DIP (upward dep) | A2 changed deps to repositories | Moderate DIP (lateral dep) | Case B |
| OCP in `CategoryDiscount` (model side) | OCP violation (`instanceof`) | A1 fixed it | Compliant | Case B |

**Conclusion:** GRASP and SOLID are orthogonal. A GRASP fix can worsen a
SOLID violation (A3 on `generateReceipt`) or fix one in passing (A2 on
`WarrantyRepository`, A1 on `CategoryDiscount`). Both audits are necessary
for a complete quality review. The full GRASP history is preserved in
`docs/grasp-refactor-log.md` and is the source of truth for this section.

---

## 4. Are clients forced to depend on methods they don't use? (ISP)

**Answer:**

Yes. There are three specific violations.

**I-01 — Two parallel stock services with divergent contracts.**

`SaleService` and `ReturnService` must depend on **both** `ProductService`
and `AccessoryService`. Each client only needs four operations
(`findById`, `hasEnoughStock`, `reduceStock`, `restoreStock`), but the two
services have different semantics on a miss:

| Operation | `ProductService` | `AccessoryService` |
|-----------|------------------|---------------------|
| `findById(String)` | Returns `null` | Throws `NoSuchElementException` |
| `hasEnoughStock(String, int)` | Returns `false` on miss | Not implemented; `findById` throws |
| `reduceStock(String, int)` | Returns `false` on miss | Not implemented; `updateStock` throws |
| `restoreStock(String, int)` | Returns `false` on miss | Returns `void` (throws on miss) |

This divergence is a fat interface from the client's perspective: they must
know which service to call based on the runtime type, which is exactly the
`instanceof` branching seen in V-08 and V-12.

**I-02 — `SaleMenu` receives an unused `WarrantyService`.**

`SaleMenu` is constructed with a `WarrantyService` dependency but only uses
it to ask about extended warranties for consoles — a decision that belongs to
`SaleService` (V-07). The menu depends on a service it barely uses.

**I-03 — `findById` contract divergence (`ProductService` vs `AccessoryService`).**

This is the sub-case that already forced `SaleService.hasEnoughStockAccessory`
to wrap the call in a `try/catch`. Same method name, different error
semantics, from the client's point of view.

---

## 5. Do high-level modules depend on abstractions or concrete implementations? (DIP)

**Answer:**

Purely on concrete implementations. There is no interface anywhere in the
codebase. The dependency graph is:

| Consumer | Concrete dependencies |
|----------|-----------------------|
| `PersonService` | `PersonRepository` |
| `ProductService` | `ProductRepository` |
| `AccessoryService` | `AccessoryRepository` |
| `PromotionService` | `PromotionRepository` |
| `WarrantyService` | `WarrantyRepository`, `ProductService` |
| `SaleService` | `SaleRepository`, `ProductService`, `PersonService`, `AccessoryService`, `PromotionService`, `WarrantyService` (6 concrete) |
| `ReturnService` | `ReturnRepository`, `SaleRepository`, `ProductService`, `AccessoryService`, `WarrantyService` (5 concrete) |
| `SaleRepository` | `ProductRepository`, `AccessoryRepository`, `PersonRepository` (3 concrete) |
| `WarrantyRepository` | `SaleRepository`, `ProductRepository`, `AccessoryRepository` (3 concrete) |
| `ReturnRepository` | `SaleRepository`, `ProductRepository`, `AccessoryRepository` (3 concrete) |
| All UI menus | Concrete services |

**Consequence:** Every dependency is a compile-time concrete reference.
Unit-testing `SaleService` requires instantiating six concrete collaborators
(all of which write to CSV on construction), and replacing any repository
with an in-memory fake requires editing the source code of the service.

The A2 GRASP fix partially improved `WarrantyRepository` (see Q3, Case B),
but did not introduce abstractions. The R8 refactor will introduce
`InventoryService` and repository interfaces without a full DI container —
constructor injection is already in place, so the change is additive.

---

## Summary of the 5 questions

| Principle | Verdict | Root cause |
|-----------|---------|------------|
| **SRP** | Violation | Presentation + orchestration mixed with domain and services |
| **OCP** | Violation | `instanceof` chains, hardcoded type sets, hardcoded categories |
| **SOLID ↔ GRASP** | Complementary | A3 worsened SRP; A1/A2 fixed some SOLID in passing |
| **ISP** | Violation | Two parallel stock services with divergent contracts; `findById` mismatch |
| **DIP** | Violation | Zero interfaces; every service depends on concrete classes |

**Note on LSP:** the Liskov Substitution Principle is audited per-class in the
individual audits (`docs/solid-audit/*.md`) and in the consolidated audit,
not in this document, because the R8 assignment defines exactly five
orienting questions and LSP is not one of them.