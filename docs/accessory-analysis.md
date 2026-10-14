# Accessory Module Analysis — GameZone Unicesar

## Module overview

The accessory module extends the base product hierarchy from Workshop 1 with three new sellable item types: controllers, cables, and memory units. Accessories are sold alongside video games and consoles, and they can be associated with compatible consoles.

## Analysis questions

### 1. Should accessories be fused into the existing product hierarchy or form a new independent hierarchy?

**Answer:** They should extend the existing `Product` hierarchy. Reasons:

- Reuse of stock, price, and description logic.
- Polymorphism: accessories can be treated as `Product` in sales and inventory.
- Integration with the same persistence and service layers without duplication.
- `Accessory` is declared as an abstract subclass of `Product`.

### 2. What attributes are common to all three accessory types, and which are specific to each?

**Answer:**

Common (inherited from `Product`):

- `id`, `title`, `price`, `quantity`

Common to all accessories (`Accessory` class):

- `compatibleConsoles: List<String>` — IDs of compatible consoles.

Specific attributes:

- **Controller:** `connectionType` (Wired, Wireless)
- **Cable:** `length`, `connectorType` (HDMI, USB, Optical)
- **Memory:** `gigabytes`, `memoryType` (SD, microSD)

This is reflected with `Accessory` as an abstract base class and `Controller`, `Cable`, `Memory` as concrete subclasses.

### 3. How is compatibility between accessory and console represented?

**Answer:** Compatibility is modeled as a **unidirectional association** stored on the accessory side:

- Each accessory holds a `List<String>` of console IDs.
- The console does not know its accessories. This avoids bidirectional coupling.
- In CSV persistence, IDs are joined with `|` (pipe) as separator.

### 4. What modifications are needed in `SaleService` to include accessories?

**Answer:**

- `registerSale` accepts `List<Product>` (accessories are `Product` subclasses).
- Stock validation delegates to `AccessoryService` when the item is an `Accessory`, and to `ProductService` otherwise.
- Inventory updates call `AccessoryService.updateStock` for accessories and `ProductService.reduceStock` for other products.
- No breaking changes to existing logic for video games and consoles.

### 5. In which layer should accessory classes be located?

**Answer:**

- `Accessory`, `Controller`, `Cable`, `Memory` → **model**
- `AccessoryRepository` → **persistence**
- `AccessoryService` → **service**
- Accessory submenu → **ui**

This respects the layered architecture `ui → service → persistence → model`.

## Classes introduced

| Class | Layer | Type | Purpose |
|-------|-------|------|---------|
| `Accessory` | model | abstract | Common base for all accessory types |
| `Controller` | model | concrete | Accessory with connection type |
| `Cable` | model | concrete | Accessory with length and connector |
| `Memory` | model | concrete | Accessory with capacity and type |
| `AccessoryRepository` | persistence | concrete | CSV persistence for accessories |
| `AccessoryService` | service | concrete | Business logic and stock management |
| `AccessoryMenu` | ui | concrete | Console submenu for accessories |

## Files touched

- `data/accessories.csv` — new file for persistence
- `SaleService` — accepts accessories as items
- `ConsoleMenu` — new accessory submenu
- `CategoryDiscount` — accepts `ACCESSORY` category (see A1)

## Related integration adjustments

- **A1:** `CategoryDiscount` recognizes accessories for category-based promotions.
- **A4:** `ReturnService` delegates stock restoration to `AccessoryService.restoreStock`.