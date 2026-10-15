# GRASP Refactor Log — GameZone Unicesar

**Team:** GameZone Unicesar  
**Reference commit:** `[hash]` (before corrections)  
**Target commit:** `[hash]` (after corrections)

---

## No-regression verification protocol

### Operations verified
All 7 menus and their sub-options (as listed in `grasp-analysis.md`).

### Reference data
- Customer: `01`
- Seller: `V-001`
- Products: `VG-001`, `CON-001`, `ACC-001`
- Sale with console + video game + accessory, with extended warranty
- Return of console + accessory

### Expected results (before corrections)
[Add here the exact output of each operation]

### Final results (after corrections)
[Add here the exact output of each operation]

---

## Change log

| Class | Change | Violation ID | Pattern applied | Commit / PR | Responsible |
|-------|--------|--------------|-----------------|-------------|-------------|
| `Category` | New enum | V-08 | Variaciones Protegidas | `refactor: add Category enum (V-08)` | Developer 2 |
| `Product` | Add `getCategory()` returning `Category` | V-08 | Variaciones Protegidas | `refactor: use Category enum in Product hierarchy (V-08)` | Developer 1 |
| `VideoGame`, `Console`, `Accessory` | Override `getCategory()` with enum | V-08 | Variaciones Protegidas | `refactor: use Category enum (V-08)` | Developer 1 |
| `CategoryDiscount` | Use `Category` enum instead of `String` | V-08 | Variaciones Protegidas | `refactor: use Category enum (V-08)` | Developer 1 |
| `PromotionService` | Use `Category.fromString()` | V-08 | Variaciones Protegidas | `refactor: use Category enum (V-08)` | Developer 2 |
| `PromotionRepository` | Serialize `Category.name()` | V-08 | Variaciones Protegidas | `refactor: use Category enum (V-08)` | Developer 2 |
| `Product` | Add `requiresBasicWarranty()`, `supportsExtendedWarranty()`, `isAccessory()` | V-02 | Polimorfismo | `refactor: add polymorphic methods to Product (V-02)` | Developer 1 |
| `Console` | Override `requiresBasicWarranty()`, `supportsExtendedWarranty()` | V-02 | Polimorfismo | `refactor: add polymorphic methods (V-02)` | Developer 1 |
| `Accessory` | Override `isAccessory()` | V-02 | Polimorfismo | `refactor: add polymorphic methods (V-02)` | Developer 1 |
| `InventoryService` | New pure fabrication | V-04, V-07 | Fabricación Pura | `refactor: introduce InventoryService (V-04, V-07)` | Developer 2 |
| `SaleService` | Use `InventoryService`, remove `instanceof` | V-02, V-04, V-05, V-07 | Bajo Acoplamiento, Polimorfismo | `refactor: reduce SaleService coupling (V-02, V-04, V-05, V-07)` | Technical Lead |
| `ReturnService` | Use `InventoryService`, remove `instanceof` | V-07 | Indirección | `refactor: use InventoryService in ReturnService (V-07)` | Technical Lead |
| `SaleMenu` | Delegate extended warranty decision to service | V-01 | Controlador | `refactor: move decision to service (V-01)` | Technical Lead |
| `Main` | Build `InventoryService`, inject into services | V-04 | Bajo Acoplamiento | `refactor: wire InventoryService (V-04)` | Technical Lead |

---

## Evidence of no regression

### Before corrections
[Run the scenario, paste output here]

### After corrections
[Run the same scenario, paste output here]

**Conclusion:** All outputs are identical. External behavior preserved.