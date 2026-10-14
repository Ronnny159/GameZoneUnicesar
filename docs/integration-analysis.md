# Integration Analysis — GameZone Unicesar

This document describes each integration adjustment (A1–A7) applied to the
system when the four modules (accessories, promotions, warranties, returns)
coexist in a single integrated system.

## A1 — Category discount for accessories

**Type:** New feature  
**Branch:** `feature/accessory-category-discount`

**Cause:** The original `CategoryDiscount` only accepted `VIDEOGAME` and
`CONSOLE` as valid categories. With the accessory module integrated, the store
needs to launch promotions on accessories.

**Solution:**
- `Product` declares an abstract `getCategory()` method.
- `VideoGame`, `Console`, and `Accessory` override it with their own category.
- `CategoryDiscount.calculateDiscount` uses `product.getCategory()` instead of
  hardcoded `instanceof` checks.
- `PromotionService.registerCategoryDiscount` validates against the three
  allowed values: `VIDEOGAME`, `CONSOLE`, `ACCESSORY`.
- A category promotion for accessories was added to `data/promotions.csv`.

## A2 — Circular dependency in the warranty module

**Type:** Fix  
**Branch:** `fix/warranty-circular-dependency`

**Cause:** The original design created the cycle
`SaleService → WarrantyService → WarrantyRepository → SaleService`, because
`WarrantyRepository` needed to resolve `Sale` references during loading.

**Solution:**
- `WarrantyRepository` now persists and loads only sale and product IDs.
- It depends on `SaleRepository` and `ProductRepository`, not on `SaleService`.
- `WarrantyService` resolves references by injecting `SaleRepository` and
  `ProductService`.
- `Main` builds the dependencies in the correct order.

## A3 — Unified sale registration flow

**Type:** Refactor  
**Branch:** `refactor/unified-sale-registration`

**Cause:** Three requirements modified `SaleService.registerSale`
independently, and the order of operations affected the result.

**Solution:** `registerSale` now executes in this order:

1. Validate at least one item.
2. Resolve each item as product or accessory and validate stock.
3. Create the sale and calculate the subtotal.
4. Query `PromotionService.findBestPromotionFor(sale)` and register the discount.
5. Generate basic warranties for consoles and extended warranties when requested.
6. Calculate final total: `subtotal − discount + extendedWarrantyCost`.
7. Update inventory (delegating to `ProductService` or `AccessoryService`).
8. Persist the sale and warranties.

`Sale.generateReceipt` shows subtotal, promotion name, discount, extended
warranty cost, and final total.

## A4 — Accessory return stock

**Type:** Fix  
**Branch:** `fix/return-accessory-stock`

**Cause:** `ReturnService` only called `ProductService.restoreStock`, which
does not manage accessory inventory.

**Solution:**
- `ReturnService` injects `AccessoryService`.
- It delegates stock restoration based on the runtime type of the returned item.
- `AccessoryService.restoreStock` was added.
- `ReturnRepository` resolves accessory references by injecting
  `AccessoryRepository`.

## A5 — Refund for discounted sales

**Type:** Fix  
**Branch:** `fix/return-discounted-refund`

**Cause:** `Return.calculateRefundAmount` summed list prices, over-refunding
when a promotion was applied.

**Solution:**
- The refund is now proportional to the original discount:
  `price × (1 − discountAmount / subtotal)`.
- `generateReturnReceipt` shows list price, proportional refund, and total per item.

## A6 — Monthly balance report

**Type:** Fix  
**Branch:** `fix/monthly-balance-report`

**Cause:** `generateMonthlyBalance` returned only the net balance.

**Solution:**
- Added `calculateMonthlySales` and `calculateMonthlyReturns`.
- `generateMonthlyBalance` returns the difference.
- The sales total uses the final total of each sale (with discount and extended
  warranty cost).
- `ReturnMenu` shows the three values.

## A7 — Warranty cancellation on console return

**Type:** New feature  
**Branch:** `feature/return-warranty-cancellation`

**Cause:** No requirement defined what happens to a console's warranty when the
console is returned.

**Solution:**
- `WarrantyService.cancelWarranties(productId, saleId)` removes all warranties
  of the product in the sale and returns the refundable amount (0 for basic,
  extended cost for extended).
- `ReturnService.registerReturn` calls this method for each returned console
  and adds the refund to the total.
- `Return` incorporates the warranty refund in the calculation and receipt.