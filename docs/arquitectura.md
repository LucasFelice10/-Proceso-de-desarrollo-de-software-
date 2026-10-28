# Arquitectura en capas (N-Tier + MVC)

El front en React es la Vista; los controladores REST son el Controlador; los servicios y patrones
son el Modelo de negocio. El front y el back se comunican solo por REST y SSE.

```mermaid
flowchart TB
    subgraph Presentacion["Presentación (Vista) - /frontend"]
        UI["App React: tabla en tiempo real, alertas en rojo, botón One-Click"]
    end

    subgraph Control["Control (Controlador) - controller"]
        CTRL["ProductoController · ProveedorController · VentaController<br/>PedidoController · EventosController (SSE)"]
    end

    subgraph Negocio["Negocio (Servicios) - service / patterns"]
        VENTA["VentaService<br/>Chain of Responsibility"]
        INV["InventarioObservable<br/>Observer + ObservadorSSE"]
        REPO["ReposicionService · GeneradorPedidos<br/>Strategy + ROP"]
        EST["EstadoPedido<br/>State"]
    end

    subgraph Datos["Acceso a datos - dao"]
        DAO["IProductoDAO · IProveedorDAO · IPedidoDAO · IMovimientoDAO"]
        FAB["DAOFactory (Abstract Factory)<br/>H2DAOFactory · MemoriaDAOFactory"]
    end

    DB[("H2 - JDBC")]

    UI -- "REST (JSON)" --> CTRL
    INV -. "SSE: eventos stock y alerta" .-> UI
    CTRL --> VENTA
    CTRL --> REPO
    CTRL --> EST
    VENTA --> INV
    REPO --> EST
    VENTA --> DAO
    INV --> DAO
    REPO --> DAO
    DAO --> FAB
    FAB --> DB
```
