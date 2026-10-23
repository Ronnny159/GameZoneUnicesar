# Consolidated SOLID Audit — GameZone Unicesuar

**Team:** GameZone Unicesuar  
**Consolidated by:** Technical Lead  
**Date:** 2026-10-21  
**Commit audited:** `develop` at the start of Requirement 8  
**Reference commit:** `[hash]`

---

## Coverage table

| Class | Audited by |
|-------|-----------|
| `Product`, `VideoGame`, `Console` | Developer 1 |
| `ProductRepository` | Developer 1 |
| `ProductService` (except `restoreStock`) | Developer 1 |
| `Accessory`, `Controller`, `Cable`, `Memory` | Developer 1 |
| `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 |
| `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 |
| `Return`, `Sale.canBeReturned` | Developer 1 |
| `exceptions`, `validation` packages | Developer 1 |
| `Person`, `Customer`, `Seller` | Developer 2 |
| `PersonRepository`, `PersonService` | Developer 2 |
| `AccessoryRepository`, `AccessoryService` | Developer 2 |
| `PromotionRepository`, `PromotionService` | Developer 2 |
| `WarrantyRepository`, `WarrantyService` | Developer 2 |
| `ReturnRepository`, `ReturnService` | Developer 2 |
| `Sale`, `SaleRepository`, `SaleService` | Technical Lead |
| `ConsoleMenu`, `Main`, all UI menus, `ConsoleUtils` | Technical Lead |
| `ProductService.restoreStock` | Technical Lead |

**Conclusion:** Every class was audited by its owner. No class was omitted or
double-audited.

---

## Final verdict per principle

| Principle | Verdict |
|-----------|---------|
| SRP | Violation (V-01 … V-07) |
| OCP | Violation (V-08 … V-11) |
| LSP | Violation (V-12) |
| ISP | Violation (V-13, V-14) |
| DIP | Violation (V-15 … V-18) |

---

## Definitive list of violations

### SRP violations

| ID | Class | Evidence | Solution | Responsible |
|----|-------|----------|----------|-------------|
| V-01 | `Sale` | `generateReceipt()` builds string | `SaleReceiptFormatter` | Tech Lead |
| V-02 | `Return` | `generateReturnReceipt()` | `ReturnReceiptFormatter` | Developer 1 |
| V-03 | `Warranty` | `generateWarrantyCertificate()` | `WarrantyCertificateFormatter` | Developer 1 |
| V-04 | `PersonService` | Constructor seeds sellers | `SellerSeeder` | Developer 2 |
| V-05 | `SaleService` | 5 responsibilities in `registerSale` | `InventoryService` + coordinator role | Tech Lead |
| V-06 | `ReturnService` | Registration + reporting | `MonthlyBalanceService` | Developer 2 |
| V-07 | `SaleMenu` | `instanceof Console` in UI | `SaleService.getExtendedWarrantyCandidates()` | Tech Lead |

### OCP violations

| ID | Class | Evidence | Solution | Responsible |
|----|-------|----------|----------|-------------|
| V-08 | `SaleService` | `instanceof Accessory` / `Console` | `Product.requiresBasicWarranty()` + `InventoryService` | Tech Lead |
| V-09 | `SaleMenu` | `instanceof Console` | Same as V-07 | Tech Lead |
| V-10a | `ProductRepository` | `instanceof VideoGame` / `Console` | `Product.toCsvFragment()` + `getTypeDiscriminator()` | Developer 1 |
| V-10b | `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` | `instanceof` chains | `Accessory.toCsvFragment()`, `Promotion.toCsvFragment()`, `Warranty.toCsvFragment()` | Developer 1 + Developer 2 |
| V-11 | `PromotionService` | Hardcoded categories | `Category` enum | Developer 2 |

### LSP violations

| ID | Class | Evidence | Solution | Responsible |
|----|-------|----------|----------|-------------|
| V-12 | `Accessory` / `ProductService` | Silent failure in `reduceStock` | `InventoryService` façade | Tech Lead + Developer 2 |

### ISP violations

| ID | Class | Evidence | Solution | Responsible |
|----|-------|----------|----------|-------------|
| V-13 | `SaleService`, `ReturnService` | Depend on two parallel stock services | `InventoryService` façade | Tech Lead + Developer 2 |
| V-14 | `ProductService` / `AccessoryService` | Divergent `findById` contracts | `InventoryService.findById → Optional` | Developer 1 + Developer 2 |

### DIP violations

| ID | Class | Evidence | Solution | Responsible |
|----|-------|----------|----------|-------------|
| V-15 | `SaleService` | 6 concrete dependencies | `InventoryService` + interfaces | Tech Lead + Developer 2 |
| V-16 | `ReturnService` | 5 concrete dependencies | `InventoryService` + interfaces | Developer 2 + Tech Lead |
| V-17a | `ProductService` | Concrete `ProductRepository` | `IProductRepository` interface | Developer 1 |
| V-17c | `AccessoryService`, `PromotionService`, `WarrantyService`, `PersonService` | Concrete repositories | `I*Repository` interfaces | Developer 2 |
| V-18 | `WarrantyRepository`, `ReturnRepository` | 3 concrete repositories each | `ISaleLookup`, `IProductLookup` | Developer 1 + Developer 2 |

---

## Border findings

### B-1: `InventoryService` (covers V-05, V-08, V-12, V-13, V-14, V-15, V-16)

**Integrantes involved:** Technical Lead (`SaleService`, `ReturnService`,
`SaleMenu`) and Developer 2 (`ProductService`, `AccessoryService`).

**Registration:** Single shared solution for seven violations across three
principles (SRP, LSP, ISP, DIP).

**Correction split:**
- **Developer 2 (first PR):** Create `InventoryService` with a uniform
  contract: `Optional<Product> findById(String)`,
  `boolean hasEnoughStock(String, int)`, `boolean reduceStock(String, int)`,
  `boolean restoreStock(String, int)`. Internally dispatches to
  `ProductService` or `AccessoryService` based on which one resolves the id.
- **Technical Lead (second PR):** Update `SaleService`, `ReturnService`,
  `SaleMenu`, and `Main` to inject `InventoryService` instead of the two
  individual services. Update `SaleService.registerSale` to call
  `Product.requiresBasicWarranty()` (added by Developer 1, see B-2) instead
  of `instanceof Console`.

**Order:** Developer 2 first, then Technical Lead.

### B-2: `toCsvFragment()` polymorphic serialization (covers V-10a, V-10b)

**Integrantes involved:** Developer 1 (model classes) and Developer 2
(accessory/promotion/warranty repositories).

**Correction split:**
- **Developer 1 (first PR):** Add `abstract String toCsvFragment()` to
  `Product`, `Accessory`, `Promotion`, `Warranty` and implement in every
  concrete subclass. Add `abstract String getTypeDiscriminator()` where
  needed. Add `Product.requiresBasicWarranty()` (default `false`, overridden
  in `Console` to `true`) — this is used by B-1 as well.
- **Developer 2 (second PR):** Update `ProductRepository` (border with
  Developer 1 — Developer 1 may also update it if he owns it, see V-10a),
  `AccessoryRepository`, `PromotionRepository`, `WarrantyRepository` to call
  the polymorphic methods instead of `instanceof`.

**Order:** Developer 1 first, then Developer 2.

### B-3: Repository interfaces (covers V-17a, V-17c, V-18)

**Integrantes involved:** Technical Lead (repository consumers),
Developer 1 (`ProductService`, `ProductRepository`, `WarrantyRepository`),
Developer 2 (`ReturnRepository`, all other repositories).

**Correction split:**
- **Developer 1 (first PR):** Define `IProductRepository`; make
  `ProductService` depend on it. Define `ISaleLookup` and `IProductLookup`;
  make `ProductRepository` and `SaleRepository` implement them.
- **Developer 2 (second PR):** Define `IAccessoryRepository`,
  `IPromotionRepository`, `IWarrantyRepository`, `IPersonRepository`,
  `IReturnRepository`. Update the corresponding services. Update
  `WarrantyRepository` and `ReturnRepository` to depend on `ISaleLookup` and
  `IProductLookup`.

**Order:** Developer 1 first, then Developer 2.

---

## Out-of-scope findings (functional defects, not SOLID)

| ID | Description | Evidence |
|----|-------------|----------|
| OF-03 | `ReturnService.registerReturn` allows the same product to be returned twice (no check against previous returns). | `ReturnService.registerReturn` does not query `returnRepository` before restoring stock. |
| OF-04 | CSV persistence does not escape `;` in titles. A product or accessory with a semicolon in its title breaks parsing. | `ProductRepository.toCsvLine`, `AccessoryRepository.toCsvLine` use `String.join(SEPARATOR, ...)` without escaping. |
| OF-05 | `SaleRepository.findById` reloads the entire CSV file on every call, with no caching. Performance issue for large histories. | `SaleRepository.findById` calls `loadAll()` which re-reads the file. |

These defects are not corrected in Requirement 8 because they would change
observable behavior (see `docs/solid-refactor-log.md`, non-regression
protocol).

**Note:** OF-01 (`ReturnRepository` orphaned returns) and OF-02
(`Sale.canBeReturned` implicit timezone) from the R7 audit are **not**
repeated here. They remain in scope from R7.
