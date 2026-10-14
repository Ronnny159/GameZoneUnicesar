# GRASP Audit — Developer 1

**Auditor:** Developer 1  
**Date:** 2026-10-14  
**Commit audited:** `develop` (before Requirement 7)

## Scope

Classes audited by Developer 1:
- `Product`, `VideoGame`, `Console`
- `Accessory`, `Controller`, `Cable`, `Memory`
- `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
- `Warranty`, `BasicWarranty`, `ExtendedWarranty`
- `Return`, `Sale.canBeReturned`

---

## 1. Experto en Información

**Verdict:** Correct

`Warranty.getAdditionalCost()` is implemented by each subclass, so each warranty knows its own cost. `Return.calculateProportionalRefund()` correctly calculates the refund based on the sale's discount.

---

## 2. Creador

**Verdict:** Correct

`Controller`, `Cable`, `Memory` are created by `AccessoryService`, which holds the list and the business rules. `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` are created by `PromotionService`. This respects the Creador pattern.

---

## 3. Controlador

**Verdict:** Not applicable

Model classes do not receive events from the system. The Controlador pattern applies to the UI/service layer, not to model classes.

---

## 4. Bajo Acoplamiento

**Verdict:** Correct

Model classes have minimal dependencies:
- `Product` depends on `Serializable` (JDK)
- `Warranty` depends on `Sale` and `Product` (necessary association)
- `Return` depends on `Sale` and `Product` (necessary association)

No unnecessary coupling.

---

## 5. Alta Cohesión

**Verdict:** Violation

### V-03 — Sale mixes domain and presentation

| Field | Content |
|-------|---------|
| **Pattern** | Alta Cohesión |
| **Module** | Sales |
| **Evidence** | `Sale.generateReceipt()` builds a formatted string with receipt details |
| **Explanation** | `Sale` should hold domain state. Formatting is presentation. |
| **Consequence** | Changing the receipt format requires modifying the domain class. |
| **Proposed solution** | Extract `ReceiptGenerator` in the service layer. |
| **Cross-integrante code** | No — `Sale` is owned by the Technical Lead |

> **Note:** This violation was already identified by the Technical Lead. It is registered here to confirm the border between our reports. The consolidation will assign it to the Technical Lead.

---

## 6. Polimorfismo

**Verdict:** Correct

- `Product.getCategory()` is abstract and implemented in each subclass
- `Promotion.calculateDiscount(Sale)` is abstract and implemented in each subclass
- `Warranty.getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()` are abstract and implemented in each subclass
- `Accessory.getAccessoryType()` is abstract and implemented in each subclass

**Example of correct application:**
```java
public abstract double calculateDiscount(Sale sale);
// PercentageDiscount: return sale.calculatePrice() * (percentage / 100);
// CategoryDiscount: iterates and sums only matching category
// BulkPurchaseDiscount: checks min quantity