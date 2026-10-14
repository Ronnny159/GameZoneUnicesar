# # GameZone Unicesar

A console-based store management system built in Java for the Programming III course at Universidad Popular del Cesar.

The system manages customers, sellers, products (video games, consoles, accessories), sales, promotions, warranties, and returns through a four-layer architecture.

---

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [Integration Adjustments](#integration-adjustments)
5. [Preloaded Data](#preloaded-data)
6. [How to Build and Run](#how-to-build-and-run)
7. [How to Test](#how-to-test)
8. [Git Workflow](#git-workflow)
9. [Documentation](#documentation)
10. [Team](#team)

---

## Features

### Base System (Workshop 1)

- Person management: register and list customers and sellers.
- Product management: register and list video games and consoles.
- Sales registration with customer and seller references.
- Purchase history by customer and by seller.
- Total revenue calculation.
- CSV persistence.

### Accessory Module (Requirement 1)

- Three accessory types: `Controller`, `Cable`, `Memory`.
- Compatibility with consoles (association stored as a list of console IDs).
- Stock management shared with the product hierarchy.
- Accessory submenu with register and list operations.

### Promotion Module (Requirement 2)

- Three promotion types:
  - `PercentageDiscount`: percentage over the full sale subtotal.
  - `CategoryDiscount`: percentage over a specific category (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`).
  - `BulkPurchaseDiscount`: percentage when the sale includes a minimum number of items.
- Automatic selection of the best active promotion at sale registration.
- Only one promotion per sale (not cumulative).
- Sale receipt shows subtotal, promotion name, discount, and final total.
- Promotion submenu for registration and listing.

### Warranty Module (Requirement 4)

- `BasicWarranty`: 6 months, no extra cost, applied automatically to every console sold.
- `ExtendedWarranty`: 12 months, cost equal to 10% of the product price, optional.
- Warranty duration computed automatically in the base constructor.
- Queries: by product and sale, all warranties, active warranties, warranties expiring soon.
- Warranty certificate generation.
- Warranty cancellation on console return (A7).

### Return Module (Requirement 3)

- Partial returns of products from a sale within 30 days.
- Validation of ownership (product must belong to the original sale).
- Automatic stock restoration (products and accessories).
- Automatic warranty cancellation on console returns.
- Proportional refund when the original sale had a promotion.
- Monthly balance report: total sales, total returns, net balance.

---

## Architecture

The system follows a strict four-layer architecture:

```
UI Layer          → com.gameZone.ui
Service Layer     → com.gameZone.service
Persistence Layer → com.gameZone.persistence
Model Layer       → com.gameZone.model
```

Allowed dependency direction:

```
UI  →  Service  →  Persistence  →  Model
```

### Layer responsibilities

| Layer | Responsibility |
|-------|----------------|
| **UI** | Console input/output. Menus and submenus. No business logic. |
| **Service** | Business rules, validation, orchestration between modules. |
| **Persistence** | CSV file access. Only layer authorized to touch the filesystem. |
| **Model** | Domain entities and domain methods. No file access, no service dependency. |

---

## Project Structure

```
src/
└── com/gameZone/
    ├── model/
    │   ├── Person.java
    │   ├── Customer.java
    │   ├── Seller.java
    │   ├── Product.java
    │   ├── VideoGame.java
    │   ├── Console.java
    │   ├── Accessory.java
    │   ├── Controller.java
    │   ├── Cable.java
    │   ├── Memory.java
    │   ├── Sale.java
    │   ├── Promotion.java
    │   ├── PercentageDiscount.java
    │   ├── CategoryDiscount.java
    │   ├── BulkPurchaseDiscount.java
    │   ├── Warranty.java
    │   ├── BasicWarranty.java
    │   ├── ExtendedWarranty.java
    │   └── Return.java
    │
    ├── persistence/
    │   ├── PersonRepository.java
    │   ├── ProductRepository.java
    │   ├── AccessoryRepository.java
    │   ├── SaleRepository.java
    │   ├── PromotionRepository.java
    │   ├── WarrantyRepository.java
    │   └── ReturnRepository.java
    │
    ├── service/
    │   ├── PersonService.java
    │   ├── ProductService.java
    │   ├── AccessoryService.java
    │   ├── SaleService.java
    │   ├── PromotionService.java
    │   ├── WarrantyService.java
    │   └── ReturnService.java
    │
    └── ui/
        ├── main.java
        ├── ConsoleMenu.java
        ├── ConsoleUtils.java
        ├── PersonMenu.java
        ├── ProductMenu.java
        ├── AccessoryMenu.java
        ├── PromotionMenu.java
        ├── SaleMenu.java
        ├── WarrantyMenu.java
        └── ReturnMenu.java

data/
├── customers.csv
├── sellers.csv
├── products.csv
├── accessories.csv
├── promotions.csv
├── sales.csv
├── warranties.csv
└── returns.csv

docs/
├── analysis.md
├── class-diagram.md
├── hierarchy-diagram.md
├── layer-diagram.md
├── integration-analysis.md
├── integrated-class-diagram.md
├── accessory-analysis.md
├── accessory-class-diagram.md
├── promotion-analysis.md
├── promotion-class-diagram.md
├── warranty-analysis.md
├── warranty-class-diagram.md
├── return-analysis.md
├── return-class-diagram.md
└── ai-usage/
    ├── leader-ai-log.md
    ├── developer1-ai-log.md
    └── developer2-ai-log.md
```

---

## Integration Adjustments

The four modules were developed independently and then integrated. The following adjustments resolve conflicts that appeared when they coexist in the same system.

| ID | Type | Description |
|----|------|-------------|
| **A1** | Feature | `CategoryDiscount` accepts `ACCESSORY` as a valid category. |
| **A2** | Fix | `WarrantyRepository` no longer depends on `SaleService`. It persists only IDs and `WarrantyService` resolves references. Breaks the circular dependency. |
| **A3** | Refactor | `SaleService.registerSale` unified: subtotal → best promotion → basic + extended warranties → final total → inventory → persist. |
| **A4** | Fix | `ReturnService` delegates stock restoration to `AccessoryService.restoreStock` for accessories. |
| **A5** | Fix | `Return.calculateRefundAmount` applies the original discount proportionally. |
| **A6** | Fix | `ReturnService` provides `calculateMonthlySales`, `calculateMonthlyReturns`, and `generateMonthlyBalance`. |
| **A7** | Feature | `WarrantyService.cancelWarranties` cancels warranties when a console is returned and refunds the extended warranty cost. |
| **A8** | Docs | Integration documentation, integrated class diagram, updated layer diagram and README. |
| **A9** | Release | Pull Request from `develop` to `main`. |

### Unified sale flow (A3)

`SaleService.registerSale` executes the following steps in strict order:

1. Validate that the sale has at least one item.
2. Resolve each item as product or accessory and validate stock.
3. Create the sale and calculate the subtotal.
4. Query `PromotionService.findBestPromotionFor(sale)` and register the discount.
5. Generate the basic warranty of each console and the extended warranties requested.
6. Calculate the final total: `subtotal − discount + extendedWarrantyCost`.
7. Update inventory, delegating to `ProductService` or `AccessoryService`.
8. Persist the sale.

### Proportional refund (A5)

When a sale had a promotion, the refund of a returned item is computed as:

```
refund = price × (1 − discountAmount / subtotal)
```

The return receipt shows the list price, the proportional refund, and the total per item.

---

## Preloaded Data

The system ships with the following CSV files under `data/`:

| File | Minimum content |
|------|-----------------|
| `customers.csv` | At least 2 customers |
| `sellers.csv` | At least 2 sellers |
| `products.csv` | At least 2 video games and 2 consoles |
| `accessories.csv` | At least one controller, one cable, and one memory |
| `promotions.csv` | At least one promotion of each type (percentage, category, bulk), including one for the `ACCESSORY` category, active during the evaluation week |
| `sales.csv` | Created automatically on first sale |
| `warranties.csv` | Created automatically when a console is sold |
| `returns.csv` | Created automatically on first return |

Warranties and returns do not require preloaded data because they are generated from sales.

---

## How to Build and Run

### Requirements

- Java 17 or higher
- Maven 3.8 or higher

### Build

```bash
mvn clean compile
```

### Run

```bash
mvn exec:java -Dexec.mainClass="com.gameZone.ui.main"
```

Or run `main.java` directly from your IDE.

### Menu overview

The main menu gives access to:

1. Person Menu (customers and sellers)
2. Product Menu (video games and consoles)
3. Accessory Menu (controllers, cables, memory)
4. Promotion Menu (register and list promotions)
5. Sale Menu (register sale, view all, customer history, seller history)
6. Warranty Menu (find, list, list active, list expiring soon)
7. Return Menu (register return, list, monthly balance)

### Sale registration flow

When registering a sale:

1. Enter the customer ID and seller ID.
2. Add items one by one (products or accessories).
3. For each console, the system asks if you want to add the extended warranty.
4. The system applies the best active promotion automatically.
5. The receipt shows subtotal, promotion, discount, extended warranty cost, and final total.

---

## How to Test

The system can be demonstrated with the following end-to-end scenario:

1. Register a sale with at least one console, one video game, and one accessory.
2. Verify that the best active promotion was applied and that the receipt shows the breakdown.
3. Assign the extended warranty to the console and consult active warranties.
4. Register the partial return of the console and the accessory.
5. Verify that stock is restored, the warranty was cancelled, and the refund is correct.
6. Consult the monthly balance.

If unit tests are included, run them with:

```bash
mvn test
```

---

## Git Workflow

The project follows a simplified Git Flow:

- `main`: stable releases only.
- `develop`: integration branch.
- Feature, fix, refactor, and docs branches derived from `develop`.

### Branch naming

| Type | Pattern | Commit prefix |
|------|---------|---------------|
| New feature | `feature/name` | `feat:` |
| Bug fix | `fix/name` | `fix:` |
| Refactor | `refactor/name` | `refactor:` |
| Documentation | `docs/name` | `docs:` |

### Rules

- No direct commits to `main` or `develop`.
- No `git push --force`.
- Every change goes through a Pull Request with cross-review.
- Commits are atomic and written in English.
- Every branch is deleted from the remote after merging.

### Module branches

| Phase | Branch |
|-------|--------|
| 1 | `feature/accessory-module` |
| 2 | `feature/promotion-module` + `feature/accessory-category-discount` (A1) |
| 3 | `feature/warranty-module` + `fix/warranty-circular-dependency` (A2) + `refactor/unified-sale-registration` (A3) |
| 4 | `feature/return-module` + `fix/return-accessory-stock` (A4) + `fix/return-discounted-refund` (A5) + `fix/monthly-balance-report` (A6) + `feature/return-warranty-cancellation` (A7) |
| 5 | `docs/integration-documentation` (A8) + PR `develop` → `main` (A9) |

---

## Documentation

All analysis and design documents are under `docs/`:

| File | Content |
|------|---------|
| `analysis.md` | Consolidated analysis of the four modules and integration adjustments. |
| `class-diagram.md` | Full class diagram in Mermaid (all layers). |
| `hierarchy-diagram.md` | Inheritance hierarchies. |
| `layer-diagram.md` | Layer diagram and allowed dependencies. |
| `integration-analysis.md` | Description of A1–A7 with cause and solution. |
| `integrated-class-diagram.md` | Full integrated diagram. |
| `accessory-analysis.md` | Analysis of the accessory module. |
| `accessory-class-diagram.md` | Class diagram of the accessory module. |
| `promotion-analysis.md` | Analysis of the promotion module. |
| `promotion-class-diagram.md` | Class diagram of the promotion module. |
| `warranty-analysis.md` | Analysis of the warranty module. |
| `warranty-class-diagram.md` | Class diagram of the warranty module. |
| `return-analysis.md` | Analysis of the return module. |
| `return-class-diagram.md` | Class diagram of the return module. |
| `ai-usage/` | AI usage logs per team member. |

### AI usage logs

Per the assignment, each team member keeps a log of their interactions with AI tools in `docs/ai-usage/`:

- `leader-ai-log.md`
- `developer1-ai-log.md`
- `developer2-ai-log.md`

Each entry includes date, tool, phase and branch, objective, query, response summary, decision, and related commit.

---

## Team

| Role | Responsibility |
|------|----------------|
| **Developer 1** | Model layer: `Accessory` and subclasses, `Promotion` and subclasses, `Warranty` and subclasses, `Return`, `Sale.canBeReturned`. Adjustments A1, A5. |
| **Developer 2** | Persistence and service layer for all modules, including best promotion selection, warranty validity, and monthly balance. Adjustments A2, A4, A6, A7. |
| **Technical Lead** | Integration in `SaleService` and `ProductService`, `Sale.generateReceipt`, `ConsoleMenu`, `README.md`, Pull Request review. Adjustments A3, A8, A9. |

---

## License

Academic project for the Programming III course (SS462) at Universidad Popular del Cesar. Instructor: Ing. Esp. Alfredo Bautista.