# AI Usage Log - Technical Lead

## Overview
This document tracks all interactions with AI tools during the development of the integrated GameZone Unicesar system. All AI usage follows the legitimate use cases defined in the assignment guidelines: code review, error explanation, improvement suggestions, concept clarification, and architecture verification.

**Developer:** Ariza Villamizar Luis Alberto  
**Role:** Technical Lead  
**Modules:** System Integration, `SaleService`, `ConsoleMenu`, `README.md`, Pull Request review  
**Tools Used:** Kimi Chat, ChatGPT (DeepSeek)

---

## Session 1: Initial Code Review and Error Correction
**Date:** 2026-09-06
**Tool:** Kimi Chat
**Duration:** ~20 minutes

### Purpose:
Review initial implementation of the base model classes to identify compilation errors and architectural violations.

### Questions Asked:
1. "Are my Product, VideoGame, and Console classes correctly implemented?"
2. "What errors do I have in my code?"

### Issues Identified:
- **Package structure**: Classes were in the wrong package. Required `com.gamezone.model`.
- **Inheritance keyword**: Used `implements` instead of `extends Product` in subclasses.
- **Abstract modifier**: Subclasses were incorrectly declared as `abstract`.
- **Missing @Override**: `toString()` method in Product class lacked `@Override`.

### Changes Applied:
1. Corrected package declaration.
2. Changed `implements` to `extends Product` in VideoGame and Console.
3. Removed `abstract` from VideoGame and Console.
4. Added `@Override` annotation to `toString()`.

### Reflection:
I wrote the code following my understanding of the requirements. Kimi helped me identify technical errors I had overlooked. I understood each correction and made the changes myself.

---

## Session 2: Documentation and Code Quality Improvement
**Date:** 2026-09-07
**Tool:** ChatGPT (DeepSeek)
**Duration:** ~25 minutes

### Purpose:
Get guidance on JavaDoc documentation standards and improve code completeness to meet assignment requirements.

### Questions Asked:
1. "What should I include in JavaDoc documentation for my model classes?"
2. "Should getDescription() include all attributes to be considered complete?"

### Feedback Received:
- All public methods (constructors, getters, setters) need JavaDoc with `@param` and `@return`.
- `getDescription()` should include ALL attributes.
- Documentation must follow the assignment's rules.
- Commit prefixes (`feat:`, `docs:`, `refactor:`) with atomic commits.

### Changes Applied:
1. Added complete JavaDoc for all public methods.
2. Enhanced `getDescription()` to include every attribute.
3. Improved `toString()` with formatted currency display.
4. Ensured consistent documentation style.

### Reflection:
ChatGPT helped me understand what "complete documentation" means. I wrote all the JavaDoc myself following the standards explained.

---

## Session 3: Final Code Review and Approval
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Final verification of model classes before moving to the persistence layer.

### Questions Asked:
1. "Is the model layer now complete and ready for persistence?"

### Feedback Received:
- All classes meet technical requirements.
- JavaDoc is complete.
- Minor suggestion: consistent attribute order in `getDescription()`.

### Changes Applied:
- Verified all requirements are met.
- Maintained the current order as it is visually clear and functional.

### Reflection:
Kimi confirmed the model layer is complete. Ready to proceed.

---

## Session 4: Persistence Layer Review
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Review the `ProductRepository` implementation to verify compliance with layered architecture and correct use of Java serialization.

### Questions Asked:
1. "What do you think of my `ProductRepository` class?"
2. "Is persistence really just a file?"

### Issues Identified:
- **Missing Serializable**: The product hierarchy did not implement `Serializable`.
- **Relative file path**: Depending on the execution directory.
- **`System.err.println` in persistence**: Violates layered architecture.
- **Missing null validation** in `save()`.
- **Directory creation**: `data/` was not created automatically.

### Changes Applied:
1. Added `implements Serializable` and `serialVersionUID`.
2. Improved directory creation.
3. Removed console prints.
4. Added null validation.
5. Used `java.nio.file.Paths`.
6. Added JavaDoc.

### Reflection:
The persistence layer should remain clean without presentation logic. Separation of concerns is fundamental.

---

## Session 5: Service Layer Review
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Review `ProductService` and the initial `SaleService`.

### Questions Asked:
1. "What do you think? Does it meet the requirements or not?"

### Feedback Received:
- Business validations well implemented.
- Correct use of defensive copying.
- Observation: `updateStock` semantically only subtracts stock.
- `SaleService` needed to accept accessories in the sale without breaking existing behavior.

### Changes Applied:
- Kept `updateStock` as is to align with the assignment.
- Extended `SaleService` to accept `List<Product>`.

### Reflection:
Method names should reflect exactly what they do. Extending without breaking is a key design skill.

---

## Session 6: Service Design Clarification
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~5 minutes

### Purpose:
Consult about whether to add a complementary method when renaming `updateStock`.

### Question Asked:
"In case of choosing the option to rename `updateStock`, should I add a method that does the opposite?"

### Response Received:
- Not mandatory according to the assignment's menu.
- Good design practice but outside the required scope.

### Decision Made:
- Opted not to add `increaseStock` nor rename the method.

### Reflection:
I learned to evaluate whether a design improvement is within or outside the scope.

---

## Session 7: Integration Error Identification and Code Quality Review
**Date:** 2026-09-27
**Tool:** ChatGPT (DeepSeek)
**Duration:** ~90 minutes

### Purpose:
As Technical Lead, review the entire integrated system (model, persistence, service, UI) to identify compilation errors, inconsistencies, and violations of the assignment requirements before the final submission. The goal was to locate exactly where the errors were in my code, not to generate new code.

### Questions Asked:
1. "Does my model layer comply with all the requirements?"
2. "Does my persistence layer comply with the required CSV format?"
3. "Are there compilation errors in my services?"
4. "Does my `main` class integrate the four modules (accessories, promotions, warranties, returns) correctly?"
5. "Does my `SaleService.registerSale` follow the unified order described in A3?"
6. "What is still missing to have the complete integrated system?"

### Issues Identified by the AI:

**Model layer:**
- `Sale.java` was missing the integration attributes `appliedPromotionName`, `discountAmount`, and `extendedWarrantyCost`.
- `Sale.java` lacked `generateReceipt()`, `calculateTotal()`, and `canBeReturned()`.
- `CategoryDiscount.calculateDiscount()` had a broken formula that modified the `percentage` attribute.
- `Warranty` constructor received `endDate` as a parameter instead of computing it.
- `Warranty.isActive(LocalDate)` and `Promotion.isActive(LocalDate)` ignored the parameter.
- `Return.calculateRefundAmount()` did not apply the proportional discount (A5).
- `Return.generateReturnReceipt()` did not show the discount breakdown.
- `Accesory` was misspelled (should be `Accessory`).
- `Person`, `Promotion`, and `Warranty` used `protected` attributes instead of `private`.

**Persistence layer:**
- Some repositories used `.dat` instead of CSV.
- `saveAll` received a single object instead of a `List` in several repositories.
- `loadAll` was private and returned void in some repositories.
- Missing type discriminator in `PromotionRepository` and `WarrantyRepository`.
- Missing `findById` in `SaleRepository` and `WarrantyRepository`.
- `SaleRepository` called a nonexistent constructor of `Sale`.
- `WarrantyRepository` and `ReturnRepository` had circular dependencies.

**Service layer:**
- `ProductService` used old methods `load()` / `save()`.
- `PromotionService.registerPercentageDiscount` received a generic `Promotion`.
- `SaleService.registerSale` had inverted stock validation logic.
- `SaleService` used `savesales` instead of `saveAll(List)`.
- `WarrantyService.assignBasicWarranty` used `List.of(warranty)` overwriting existing warranties.
- `ReturnService` did not exist.

**UI layer:**
- `main` did not build dependencies in the correct order.
- `main` called `new ProductService()` without dependencies.
- `main` called `new SaleRepository()` without dependencies.
- `main` called `saleService.registerSale(sale)` with the old signature.
- No submenus for promotions, warranties, or returns.
- No monthly balance option.
- No question about extended warranty when selling consoles.

**Integration specific (A3):**
- The unified flow of `registerSale` was not respected: subtotal → best promotion → basic + extended warranties → total → inventory → persist.
- `Sale.generateReceipt` did not show subtotal, promotion name, discount, extended warranty cost, and final total.

**Git flow:**
- The feature branches were not being merged in the required order (accessories → promotions → warranties → returns → closure).
- No documentation files existed yet in `docs/`.
- No AI usage logs existed in `docs/ai-usage/`.

### Actions Taken by Me (Technical Lead):
1. Replaced `Sale.java` with the version that includes the integration attributes and methods.
2. Rewrote `CategoryDiscount.calculateDiscount()` following the correct formula.
3. Fixed the `Warranty` constructor to compute `endDate` internally.
4. Corrected `isActive` in `Warranty` and `Promotion` to use the parameter.
5. Updated `Return.calculateRefundAmount()` and `generateReturnReceipt()`.
6. Renamed `Accesory` to `Accessory` across all files.
7. Changed `protected` attributes to `private`.
8. Migrated `.dat` repositories to CSV with type discriminators.
9. Fixed `saveAll` and `loadAll` signatures in all repositories.
10. Injected dependencies in `SaleRepository`, `WarrantyRepository`, and `ReturnRepository`.
11. Corrected `ProductService`, `PromotionService`, `WarrantyService`.
12. Created `ReturnService`.
13. Rewrote `SaleService.registerSale` with the unified flow (A3).
14. Refactored `main` into multiple UI classes with proper dependency construction.
15. Added the four new submenus.
16. Organized the branch order per Git Flow.
17. Started drafting the integration documentation (`docs/integration-analysis.md`).

### Reflection:
The AI did not provide code for me. It only pointed out where my errors were, explained why each was a problem, and described what the correct behavior should be. I located each error in my own code, understood the cause, and applied the corrections myself. As Technical Lead, this session was the most valuable because it forced me to trace the inconsistencies across all four layers and across the four modules.

---

## Summary of AI Usage Patterns

| Type of Use | Frequency | Legitimate? |
|-------------|-----------|-------------|
| Code review and error identification | 5 sessions | Yes (allowed per point 16) |
| Improvement suggestions | 3 sessions | Yes (allowed per point 16) |
| Documentation guidance | 2 sessions | Yes (allowed per point 16) |
| Architecture verification | 3 sessions | Yes (allowed per point 16) |
| Scope management consultation | 1 session | Yes (allowed per point 16) |
| Integration review | 1 session | Yes (allowed per point 16) |
| Code generation | 0 sessions | No code copied |

---

## Key Learnings

1. **Java Serialization**: Requires the entire class hierarchy to implement `Serializable`.
2. **Separation of Concerns**: Persistence layer should not print to the console.
3. **Defensive Programming**: Always return copies of internal lists.
4. **Method Naming**: Method names should accurately reflect what they do.
5. **Scope Management**: Evaluate whether additional features are within project scope.
6. **Validation**: Always validate inputs and business rules before processing.
7. **Integration**: The four modules must respect a strict order of operations.
8. **Layered Architecture**: Dependencies must flow only from UI to service to persistence to model.
9. **Unified Sale Flow (A3)**: Order of operations determines the final total of a sale.
10. **Circular Dependencies (A2)**: Repositories should persist IDs and let services resolve references.

---

## Declaration

**I confirm that all code in this module was written by me.** AI tools were used only for review, suggestions, concept clarification, and architecture verification. I never copied complete implementations from AI tools. Every change I applied was understood and implemented by me.

---

## Tools Used Summary

| Tool | Purpose | Sessions |
|------|---------|----------|
| Kimi Chat | Primary supervisor, error identification, compliance checking, architecture verification | 5 |
| ChatGPT (DeepSeek) | Documentation guidance, completeness verification, alternative perspective, integration review | 2 |