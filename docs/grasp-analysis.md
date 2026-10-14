# GRASP Analysis — GameZone Unicesar

**Team:** GameZone Unicesar  
**Course:** Programming III  
**Instructor:** Ing. Esp. Alfredo Bautista  
**Commit audited:** `develop` at the start of Requirement 7

---

## 1. What criteria did the team use to decide whether a code fragment is a GRASP violation, a correct application, or not applicable?

**Answer:**

We defined three veredicts with objective criteria:

### Violation
A code fragment is a violation when:
- The responsibility is assigned to a class that does not hold the information (Experto en Información)
- A class instantiates another when it should not (Creador)
- The UI executes domain logic (Controlador)
- A class has too many responsibilities (Alta Cohesión)
- Two classes are tightly coupled through concrete types (Bajo Acoplamiento)
- `instanceof` or type-based `if/else` chains appear (Polimorfismo)
- A responsibility is placed in a class that does not belong to the domain, but no pure fabrication was introduced (Fabricación Pura)
- Two components communicate directly when they should go through an intermediary (Indirección)
- A foreseeable change (new category, new type) requires modifying multiple classes (Variaciones Protegidas)

### Correct
The pattern is correctly applied when:
- The class holding the responsibility is the one with the necessary information
- The subclass overrides abstract methods via polymorphism
- The UI only delegates to services
- Dependency injection is used through constructors

### Not applicable
The pattern does not apply when:
- The class does not receive events from the system (Controlador in model classes)
- There is no alternative implementation for the behavior (Polimorfismo in utility classes)

### Minimum evidence
Each veredict must include: class, method, exact line or fragment, and an explanation of why it violates or correctly applies the pattern, plus the concrete consequence.

---

## 2. What border violations did the team identify? How were they registered?

**Answer:**

We identified one border violation:

**V-04 (Bajo Acoplamiento — `SaleService`)**

This violation crosses two integrantes' code:
- The Technical Lead wrote `SaleService` (the class with 6 dependencies)
- Developer 2 wrote the services injected (`ProductService`, `PromotionService`, `WarrantyService`)

**Registration:** It was registered as a single violation in the consolidated audit. The correction was split:
- Technical Lead: reduce dependencies by introducing a `InventoryService` (Fabricación Pura)
- Developer 2: expose the required methods through the new service

---

## 3. Did the team find fragments violating more than one pattern?

**Answer:**

Yes. Two fragments violate multiple patterns simultaneously:

**V-02 + V-08 (Polimorfismo + Variaciones Protegidas)**
- `SaleService.registerSale` uses `instanceof Console` and `instanceof Accessory`
- `PromotionService.registerCategoryDiscount` uses hardcoded `equalsIgnoreCase` for categories
- Both violate the same underlying principle: no polymorphism for foreseeable variation

**Registration:** Registered as two separate violations (V-02 and V-08) because they have different solutions, but the guiding pattern for the correction is **Polimorfismo** in both cases.

**V-01 + V-06 (Controlador + Fabricación Pura)**
- `SaleMenu` both decides (Controlador violation) and orchestrates dependencies (Fabricación Pura violation)
- Registered as two violations with the same correction: move the decision to `SaleService`

---

## 4. What strategy does the team follow to preserve external behavior?

**Answer:**

We defined a **no-regression verification protocol** before starting corrections:

### Operations verified
All 7 menus and their sub-options:
1. Person Menu: Register, List Customers, List Sellers
2. Product Menu: Register VideoGame, Register Console, List Products
3. Accessory Menu: Register Cable, Register Controller, Register Memory, List, List By Type, Find Compatible
4. Promotion Menu: Register Percentage, Register Category, Register Bulk, List All, List Active
5. Sale Menu: Register Sale, View All, Customer History, Seller History
6. Warranty Menu: Find, List All, List Active, List Expiring Soon
7. Return Menu: Register Return, View All, By Customer, By Sale, Monthly Balance

### Reference data
- Customer: `01`
- Seller: `V-001`
- Products: `VG-001`, `CON-001`, `ACC-001`
- Sale with console + video game + accessory, with extended warranty
- Return of console + accessory

### Expected results
Each operation was run before corrections and its output was saved to `docs/grasp-refactor-log.md`. After each correction, the same operations were re-run and the outputs compared.

### Evidence
The initial and final outputs are stored in `docs/grasp-refactor-log.md` with the exact command and observed result.

---

## 5. In what order are violations corrected and how is cross-integrante work coordinated?

**Answer:**

Order (from lowest to highest risk):

1. **V-09** — `SellerFactory` (new class, no impact)
2. **V-08** — `Promotion` category polymorphism (affects only `CategoryDiscount`)
3. **V-03** — Extract `ReceiptGenerator` (affects only `Sale`)
4. **V-05** — Move warranty cost calculation to `Warranty` (affects `SaleService`)
5. **V-02** — Add `requiresBasicWarranty()` to `Product` (affects `SaleService`)
6. **V-07** — Introduce `InventoryService` (affects `ReturnService`, `SaleService`)
7. **V-04** — Reduce `SaleService` dependencies (affects `Main`, `SaleService`)
8. **V-01 + V-06** — Move decision to service (affects `SaleMenu`, `SaleService`)

**Cross-integrante coordination:**
- Violations affecting two integrantes' code (V-04, V-07) are corrected in **two coordinated PRs**:
  - First PR: introduce the new class (by the owner of the new responsibility)
  - Second PR: update callers (by the owner of the affected code)
- The Technical Lead coordinates the order and reviews both PRs.