# AI Usage Log - Developer 1 (Product Module)

## Overview
This document tracks all interactions with AI tools during the development of the Product Module for GameZone Unicesar. All AI usage follows the legitimate use cases defined in the assignment guidelines: code review, error explanation, improvement suggestions, concept clarification, and architecture verification.

**Developer:** Ariza Villamizar Luis Alberto  
**Module:** Products  
**Tools Used:** Kimi Chat, ChatGPT (DeepSeek)

---

## Session 1: Initial Code Review and Error Correction
**Date:** 2026-09-06
**Tool:** Kimi Chat
**Duration:** ~20 minutes

### Purpose:
Review initial implementation of product model classes to identify compilation errors and architectural violations.

### Questions Asked:
1. "Are my Product, VideoGame, and Console classes correctly implemented?"
2. "What errors do I have in my code?"

### Issues Identified:
- **Package structure**: Classes were in wrong package. Required `com.gamezone.model`.
- **Inheritance keyword**: Used `implements` instead of `extends Product` in subclasses.
- **Abstract modifier**: Subclasses were incorrectly declared as `abstract`.
- **Missing @Override**: `toString()` method in Product class lacked `@Override` annotation.

### Changes Applied:
1. Corrected package declaration to `package com.gamezone.model;`
2. Changed `implements` to `extends Product` in VideoGame and Console
3. Removed `abstract` keyword from VideoGame and Console
4. Added `@Override` annotation to `toString()` method

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
1. "What should I include in JavaDoc documentation for my Product classes?"
2. "Should getDescription() include all attributes to be considered 'complete'?"

### Feedback Received:
- **JavaDoc requirements**: All public methods (constructors, getters, setters) need JavaDoc with @param and @return.
- **getDescription() completeness**: Should include ALL attributes (id, title, price, stock, plus specific attributes) to be truly "complete" as required by the assignment.
- **Documentation**: AI usage must be documented per assignment requirements (page 16, points 15 and 18).
- **Commit strategy**: Should use proper commit prefixes (feat:, docs:, refactor:) and atomic commits.

### Changes Applied:
1. Added complete JavaDoc for ALL public methods.
2. Enhanced `getDescription()` in VideoGame and Console to include id, title, price, stock, and specific attributes.
3. Improved `toString()` with formatted currency display using `String.format("%.2f", price)`.
4. Ensured consistent documentation style across all three classes.

### Reflection:
ChatGPT helped me understand what "complete documentation" means in a professional Java project. The suggestion to include all attributes in getDescription() made the method truly useful for displaying products to users. I wrote all the JavaDoc myself following the examples and standards explained.

---

## Session 3: Final Code Review and Approval
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Final verification of model classes before moving to persistence layer.

### Questions Asked:
1. "Is the model layer now complete and ready for persistence?"

### Feedback Received:
- All classes meet technical requirements
- JavaDoc is complete for all classes and methods
- getDescription() now includes all attributes
- Minor suggestion: Consider consistent attribute order in getDescription() for visual coherence

### Changes Applied:
- Verified all requirements are met
- Decided to maintain current order as it's visually clear and functional

### Reflection:
Kimi confirmed the model layer is complete and meets all specifications. Ready to proceed to persistence layer.

---

## Session 4: Persistence Layer Review
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Review ProductRepository implementation to verify compliance with layered architecture and correct use of Java object serialization.

### Questions Asked:
1. "What do you think of my ProductRepository class?"
2. "Is persistence really just a file?"

### Issues Identified:
- **Missing Serializable**: The product hierarchy did not implement Serializable, which would cause `NotSerializableException`.
- **Relative file path**: The path `data/products.dat` depends on the execution directory.
- **System.err.println in persistence**: The persistence layer should not print to the console.
- **Missing null validation**: The `save()` method should validate that the products list is not null.
- **Directory creation**: The `data/` directory was not being created automatically.

### Changes Applied:
1. Added `implements Serializable` and `serialVersionUID` to Product, VideoGame, and Console.
2. Improved directory creation with error handling.
3. Removed `System.err.println` from `load()`.
4. Added null validation in `save()`.
5. Used `java.nio.file.Paths` to build file path more cleanly.
6. Added JavaDoc for all public methods.

### Reflection:
I learned that Java serialization requires the entire class hierarchy to be marked as Serializable. I also learned that the persistence layer should remain clean without presentation logic.

---

## Session 5: Service Layer Review
**Date:** 2026-09-07
**Tool:** Kimi Chat
**Duration:** ~15 minutes

### Purpose:
Review ProductService to verify it meets requirements for product registration, listing, and stock updates.

### Questions Asked:
1. "What do you think? Does it meet the requirements or not?"

### Feedback Received:
- The service meets the mandatory methods.
- Business validations well implemented.
- Correct use of defensive copying in `getAllProducts()`.
- Observation: The name `updateStock` semantically only subtracts stock.

### Changes Applied:
- Decided to keep the `updateStock` method with its current behavior.

### Reflection:
I learned that method names should reflect exactly what they do.

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
- It would be good design practice but goes beyond the required scope.

### Decision Made:
- Opted not to add `increaseStock` nor rename the method.

### Reflection:
I learned to evaluate whether a design improvement is within or outside the scope of a project.

---

## Session 7: Integration Error Identification and Code Quality Review
**Date:** 2026-09-27
**Tool:** ChatGPT (DeepSeek)
**Duration:** ~60 minutes

### Purpose:
Review the entire integrated system (model, persistence, service, UI) to identify compilation errors, inconsistencies, and violations of the assignment requirements before the final submission. The goal was to locate exactly where the errors were in my code, not to generate new code.

### Questions Asked:
1. "Does my model layer comply with all requirements?"
2. "Does my persistence layer comply with the required CSV format?"
3. "Are there compilation errors in my services?"
4. "Does my main class integrate the four modules (accessories, promotions, warranties, returns) correctly?"
5. "What is still missing to have the complete system?"

### Issues Identified by the AI:

**Model layer:**
- `Sale.java` was missing the integration attributes `appliedPromotionName`, `discountAmount`, and `extendedWarrantyCost`.
- `Sale.java` lacked the methods `generateReceipt()`, `calculateTotal()`, and `canBeReturned()`.
- `CategoryDiscount.calculateDiscount()` had a broken formula that was modifying the `percentage` attribute.
- `Warranty` constructor was receiving `endDate` as a parameter instead of computing it from `getDurationInMonths()`.
- `Warranty.isActive(LocalDate)` and `Promotion.isActive(LocalDate)` were ignoring the parameter and using `LocalDate.now()` internally.
- `Return.calculateRefundAmount()` was not applying the proportional discount required by A5.
- `Return.generateReturnReceipt()` did not show the discount breakdown.
- The class `Accesory` was misspelled (should be `Accessory`).
- `Person`, `Promotion`, and `Warranty` used `protected` attributes instead of `private`.

**Persistence layer:**
- Some repositories used `.dat` serialization instead of CSV.
- `saveAll` in several repositories received a single object instead of a `List`.
- `loadAll` was private and returned void in some repositories.
- Missing type discriminator in `PromotionRepository` and `WarrantyRepository`.
- Missing `findById` in `SaleRepository` and `WarrantyRepository`.
- `SaleRepository` called a constructor of `Sale` that did not exist.
- `WarrantyRepository` and `ReturnRepository` had circular dependencies.

**Service layer:**
- `ProductService` used old methods `load()` / `save()` that no longer existed.
- `PromotionService.registerPercentageDiscount` received a generic `Promotion` instead of specific parameters.
- `SaleService.registerSale` had inverted stock validation logic.
- `SaleService` used old method `savesales` instead of `saveAll(List)`.
- `WarrantyService.assignBasicWarranty` used `List.of(warranty)` which overwrote existing warranties.
- `ReturnService` did not exist yet.

**UI layer:**
- `main` did not build the dependencies in the correct order.
- `main` called `new ProductService()` without dependencies.
- `main` called `new SaleRepository()` without dependencies.
- `main` called `saleService.registerSale(sale)` with the old signature.
- No submenus for promotions, warranties, or returns.
- No monthly balance option.
- No question about extended warranty when selling consoles.

### Actions Taken by Me (Developer):
1. Replaced `Sale.java` with the version that includes the integration attributes and methods.
2. Rewrote `CategoryDiscount.calculateDiscount()` following the correct formula.
3. Fixed `Warranty` constructor to compute `endDate` internally.
4. Corrected `isActive` in both `Warranty` and `Promotion` to use the parameter.
5. Updated `Return.calculateRefundAmount()` and `generateReturnReceipt()` with the proportional discount.
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

### Reflection:
The AI did not provide code for me. It only pointed out where my errors were, explained why each was a problem, and described what the correct behavior should be. I located each error in my own code, understood the cause, and applied the corrections myself. This session was the most valuable because it forced me to trace the inconsistencies across all four layers of the architecture.

---

## Summary of AI Usage Patterns

| Type of Use | Frequency | Legitimate? |
|-------------|-----------|-------------|
| Code review and error identification | 5 sessions | Yes (allowed per point 16) |
| Improvement suggestions | 3 sessions | Yes (allowed per point 16) |
| Documentation guidance | 2 sessions | Yes (allowed per point 16) |
| Architecture verification | 3 sessions | Yes (allowed per point 16) |
| Scope management consultation | 1 session | Yes (allowed per point 16) |
| Code generation | 0 sessions | No code copied |

---

## Key Learnings

1. **Java Serialization**: Requires entire class hierarchy to implement `Serializable` with `serialVersionUID`.
2. **Separation of Concerns**: Persistence layer should not print to console.
3. **Defensive Programming**: Always return copies of internal lists.
4. **Method Naming**: Method names should accurately reflect what they do.
5. **Scope Management**: Evaluate whether additional features are within project scope.
6. **Validation**: Always validate input parameters and business rules before processing.
7. **Integration**: The four modules must respect a strict order of operations when integrated.
8. **Layered Architecture**: Dependencies must flow only from UI to service to persistence to model.

---

## Declaration

**I confirm that all code in this module was written by me.** AI tools were used only for review, suggestions, concept clarification, and architecture verification. I never copied complete implementations from AI tools. Every change I applied was understood and implemented by me.

---

## Tools Used Summary

| Tool | Purpose | Sessions |
|------|---------|----------|
| Kimi Chat | Primary supervisor, error identification, compliance checking, architecture verification | 5 |
| ChatGPT (DeepSeek) | Documentation guidance, completeness verification, alternative perspective, integration error identification | 2 |