# SOLID Audit — Developer 1

**Auditor:** Developer 1  
**Date:** 2026-10-22  
**Commit audited:** `develop` at the start of Requirement 8  

## Scope

Classes audited by Developer 1:

- `Product`, `VideoGame`, `Console`
- `ProductRepository`, `ProductService` (except `restoreStock`, owned by Technical Lead)
- `Accessory`, `Controller`, `Cable`, `Memory`
- `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
- `Warranty`, `BasicWarranty`, `ExtendedWarranty`
- `Return`, `Sale.canBeReturned`
- Packages `com.gameZone.exceptions` and `com.gameZone.validation` (or their current equivalents)
- Classes created or modified by Developer 1 during the R7 GRASP correction
  (see `docs/grasp-refactor-log.md`)

---

## 1. Single Responsibility Principle (SRP)

**Verdict:** Violation

### V-02 — `Return` mixes domain state with presentation

| Field | Content |
|-------|---------|
| **ID** | V-02 |
| **Principle** | SRP |
| **Module** | Returns (model) |
| **Evidence** | `Return.generateReturnReceipt()` builds a `StringBuilder` with the return receipt layout (id, sale id, date, per-product list, reason, total refund) |
| **Explanation** | `Return` should hold return state. Receipt formatting is presentation. |
| **Consequence** | Any receipt-format change forces an edit of the domain entity. |
| **Proposed solution** | Extract `ReturnReceiptFormatter` in `com.gameZone.presentation`. `Return` exposes getters only. |
| **Cross-integrante code** | No |

### V-03 — `Warranty` mixes domain state with presentation

| Field | Content |
|-------|---------|
| **ID** | V-03 |
| **Principle** | SRP |
| **Module** | Warranties (model) |
| **Evidence** | `Warranty.generateWarrantyCertificate()` returns a formatted multi-line certificate |
| **Explanation** | Same issue as V-02, applied to the abstract base class. |
| **Consequence** | Any certificate-format change forces an edit of `Warranty` and (potentially) all subclasses. |
| **Proposed solution** | Extract `WarrantyCertificateFormatter` in `com.gameZone.presentation`. |
| **Cross-integrante code** | No |

**Note on `Product.getDescription()`:** this method is **not** flagged as an
SRP violation. It is a domain-level description contract, not a receipt
format. It returns a single-line product description that the UI prints
directly. Consistency across the hierarchy is its responsibility and it holds
only that responsibility.

---

## 2. Open/Closed Principle (OCP)

**Verdict:** Violation

### V-10a — `ProductRepository` uses `instanceof`

| Field | Content |
|-------|---------|
| **ID** | V-10a |
| **Principle** | OCP |
| **Module** | Products (persistence) |
| **Evidence** | `ProductRepository.toCsvLine(Product)` branches on `instanceof VideoGame` / `instanceof Console`. `fromCsvLine` switches on the string `type` |
| **Explanation** | Adding a `Headset` subtype requires editing `ProductRepository`. |
| **Consequence** | Persistence layer is not closed for modification. |
| **Proposed solution** | Add `abstract String toCsvFragment()` to `Product`; override in `VideoGame`, `Console`, `Accessory` (and subclasses). Add `abstract String getTypeDiscriminator()`. Repositories call the polymorphic methods. |
| **Cross-integrante code** | No (the consuming repositories for `Accessory` are Developer 2's — see V-10b) |

### V-10b — `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` use `instanceof` (border with Developer 2)

| Field | Content |
|-------|---------|
| **ID** | V-10b |
| **Principle** | OCP |
| **Module** | Accessories, Promotions, Warranties (persistence) |
| **Evidence** | All three repositories branch on `instanceof` in `toCsvLine` and switch on `type` in `fromCsvLine`. |
| **Explanation** | Same root cause as V-10a. The fix requires the model classes (Developer 1's territory) to expose polymorphic methods that the repositories (Developer 2's territory) consume. |
| **Consequence** | Adding a new accessory/promotion/warranty subtype requires editing the corresponding repository. |
| **Proposed solution** | Same as V-10a: `Accessory.toCsvFragment()`, `Promotion.toCsvFragment()`, `Warranty.toCsvFragment()` + `getTypeDiscriminator()`. Developer 1 adds the model methods; Developer 2 updates the repositories. |
| **Cross-integrante code** | Yes — Developer 2 owns the three repositories |

---

## 3. Liskov Substitution Principle (LSP)

**Verdict:** Correct for the model classes I own

The model layer itself does not violate LSP:

- `VideoGame`, `Console`, `Controller`, `Cable`, `Memory` correctly implement
  `getCategory()` and `getDescription()`. Any `Product` can be passed to
  `getDescription()` or `getCategory()` without surprise.
- `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` correctly
  implement `calculateDiscount(Sale)`.
- `BasicWarranty`, `ExtendedWarranty` correctly implement
  `getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()`.

**However**, `Accessory` participates in the LSP violation reported by the
Technical Lead as **V-12**: the failure is not in the `Accessory` class itself
but in `ProductService` / `ProductRepository`, which cannot manage an
`Accessory` as a `Product`. I confirm the border: V-12 belongs to the
Technical Lead and Developer 2, not to this report.

---

## 4. Interface Segregation Principle (ISP)

**Verdict:** Violation (border with Developer 2)

### V-14 — `ProductService.findById` returns `null` while `AccessoryService.findById` throws

| Field | Content |
|-------|---------|
| **ID** | V-14 |
| **Principle** | ISP |
| **Module** | Products (service) |
| **Evidence** | `ProductService.findById` returns `null` on miss. `AccessoryService.findById` throws `NoSuchElementException`. Both are injected into `SaleService` and `ReturnService`. |
| **Explanation** | Same method name, opposite error semantics. Consumers must handle both contracts. |
| **Consequence** | `SaleService.hasEnoughStockAccessory` wraps the accessory lookup in a `try/catch`; the equivalent product lookup does not need it. Divergent contracts force clients to know which service they are calling. |
| **Proposed solution** | Normalize via `InventoryService.findById(String): Optional<Product>`. Both underlying services delegate to the façade. |
| **Cross-integrante code** | Yes — Developer 2 owns `AccessoryService` |

**Note on the `exceptions` and `validation` packages:** these are utility
classes (one purpose each, no state). Audited as **Not applicable** for ISP,
because they do not implement any client-facing interface.

---

## 5. Dependency Inversion Principle (DIP)

**Verdict:** Violation

### V-17a — `ProductService` depends on the concrete `ProductRepository`

| Field | Content |
|-------|---------|
| **ID** | V-17a |
| **Principle** | DIP |
| **Module** | Products (service) |
| **Evidence** | `public ProductService(ProductRepository repository)` |
| **Explanation** | Concrete dependency. No abstraction between the service and the repository. |
| **Consequence** | Unit-testing `ProductService` requires a real `ProductRepository` that writes to `data/products.csv`. |
| **Proposed solution** | Extract `IProductRepository` interface; `ProductService` depends on it. `ProductRepository` implements it. |
| **Cross-integrante code** | No |

### V-17b — `ProductRepository` depends on concrete model classes (acceptable)

`ProductRepository` depends on `Product`, `VideoGame`, `Console`. This is a
**correct** application of DIP within the layered architecture: persistence
is allowed to depend on the model. **Verdict: correct.**

**Note on the `exceptions` and `validation` packages:** these are utility
classes with no outward dependencies. Audited as **Not applicable** for DIP.

---

## Summary of violations found by Developer 1

| ID | Principle | Class | Severity |
|----|-----------|-------|----------|
| V-02 | SRP | `Return` | Medium |
| V-03 | SRP | `Warranty` | Low |
| V-10a | OCP | `ProductRepository` | Medium |
| V-10b | OCP | `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` | Medium (border) |
| V-14 | ISP | `ProductService` / `AccessoryService` | Medium (border) |
| V-17a | DIP | `ProductService` | Medium |