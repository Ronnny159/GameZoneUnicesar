# Accessory Module Class Diagram — GameZone Unicesar

```mermaid
classDiagram
    direction TB

    class Product {
        <<abstract>>
        - String id
        - String title
        - double price
        - int quantity
        + String getDescription()*
    }

    class Accessory {
        <<abstract>>
        - List~String~ compatibleConsoles
        + List~String~ getCompatibleConsoles()
        + void setCompatibleConsoles(List~String~ compatibleConsoles)
        + void addCompatibleConsole(String consoleId)
        + boolean isCompatibleWith(String consoleId)
        + String getAccessoryType()*
        + String getDescription()
    }

    class Controller {
        - String connectionType
        + String getConnectionType()
        + void setConnectionType(String connectionType)
        + String getAccessoryType()
        + String getDescription()
    }

    class Cable {
        - double length
        - String connectorType
        + double getLength()
        + void setLength(double length)
        + String getConnectorType()
        + void setConnectorType(String connectorType)
        + String getAccessoryType()
        + String getDescription()
    }

    class Memory {
        - int gigabytes
        - String memoryType
        + int getGigabytes()
        + void setGigabytes(int gigabytes)
        + String getMemoryType()
        + void setMemoryType(String memoryType)
        + String getAccessoryType()
        + String getDescription()
    }

    class AccessoryRepository {
        + void saveAll(List~Accessory~ accessories)
        + List~Accessory~ loadAll()
    }

    class AccessoryService {
        + Controller registerController(...)
        + Cable registerCable(...)
        + Memory registerMemory(...)
        + List~Accessory~ listAllAccessories()
        + List~Accessory~ listAccessoriesByType(String type)
        + List~Accessory~ findAccessoriesCompatibleWith(String consoleId)
        + Accessory findById(String id)
        + void updateStock(String accessoryId, int quantity)
        + void restoreStock(String accessoryId, int quantity)
    }

    class AccessoryMenu {
        + AccessoryMenu(AccessoryService accessoryService, ProductService productService, Scanner input)
        + void show()
    }

    Product <|-- Accessory : extends
    Accessory <|-- Controller : extends
    Accessory <|-- Cable : extends
    Accessory <|-- Memory : extends

    AccessoryService ..> AccessoryRepository : depends on
    AccessoryService ..> Accessory : manages
    AccessoryMenu ..> AccessoryService : uses
    AccessoryMenu ..> ProductService : uses

    note for Accessory "Abstract base class for accessories"
    note for Controller "Concrete accessory type"
    note for Cable "Concrete accessory type"
    note for Memory "Concrete accessory type"
```

## Relationships

| Relationship | Between | Type |
|--------------|---------|------|
| Inheritance | `Product` → `Accessory` | `extends` |
| Inheritance | `Accessory` → `Controller`, `Cable`, `Memory` | `extends` |
| Dependency | `AccessoryService` → `AccessoryRepository` | uses |
| Dependency | `AccessoryMenu` → `AccessoryService` | uses |
| Dependency | `AccessoryMenu` → `ProductService` | uses |
| Association | `Accessory` → `Console` (as `List<String>` IDs) | references |