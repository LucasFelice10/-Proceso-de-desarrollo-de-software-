# Nexus Software - Sistema de Gestión de Stock y Automatización de Pedidos

> **Materia:** Proceso y Desarrollo de Software - UADE  
> **Grupo:** JAVA Team  
> **Trabajo:** TPO - Entrega final

Sistema para una distribuidora de bebidas que monitorea el stock en tiempo real, avisa cuando un producto cruza su umbral crítico y automatiza la reposición: calcula cuánto pedir, elige al proveedor según un perfil configurable y genera el pedido con un solo click.

---

## 🎯 Qué hace el sistema

- **Registra ventas** validando el stock físico antes de descontarlo.
- **Avisa en tiempo real** al front cuando cambia el stock. Si un producto cruza el umbral crítico, la fila se pone en rojo, sin polling.
- **Calcula el Punto de Pedido (ROP) dinámico** a partir de la venta diaria, el lead time histórico y el stock de seguridad.
- **Elige proveedor** según el perfil activo: Precio, Velocidad o Balanceada (60% precio, 40% velocidad).
- **Genera el pedido One-Click** con el mensaje armado para el proveedor (SKU y cantidad calculada) y un link tipo `wa.me`. El envío se simula.
- **Sigue el ciclo del pedido:** Generado → Enviado → Demorado (más de 2 días) → Recibido. Al recibirse se suma el stock y se apaga la alerta.

---

## 🧩 Patrones de diseño

| Patrón | Dónde se usa | Por qué |
| :--- | :--- | :--- |
| **Observer** | `InventarioObservable`, `ObservadorSSE` | El inventario avisa al front cuando cambia el stock, sin polling |
| **Chain of Responsibility** | `ValidarStockFisico` → `DescontarStock` → `VerificarUmbral` → `DispararAlerta` | Cada paso de la venta es una clase independiente |
| **Strategy** | `EstrategiaPrecio`, `EstrategiaVelocidad`, `EstrategiaBalanceada` | Agregar un perfil de selección no obliga a tocar `GeneradorPedidos` |
| **State** | `Generado`, `Enviado`, `Demorado`, `Recibido` | Ciclo de vida del pedido de reposición |
| **DAO + Abstract Factory** | `DAOFactory`, `H2DAOFactory`, `MemoriaDAOFactory` | Cambiar de motor de datos sin tocar el código de negocio |

**Punto de Pedido dinámico:** `ROP = d · L + SS`, con `SS = z · σL · d` (d = venta diaria promedio, L = lead time promedio, σL = desvío estándar de las demoras, z = factor de nivel de servicio).

---

## 🏗️ Arquitectura

Arquitectura en capas **N-Tier + MVC**. El front y el back son independientes y se comunican solo por REST y SSE.

| Capa | Contenido |
| :--- | :--- |
| Presentación (Vista) | App React: tabla en tiempo real, alertas en rojo, botón One-Click |
| Control (Controlador) | Controladores REST y endpoint SSE |
| Negocio (Servicios) | Venta, reposición, ROP, estrategias, estados del pedido |
| Acceso a datos | DAOs JDBC y Abstract Factory sobre H2 |

```
/backend    → Java 17 + Maven (Spring Boot solo como capa REST)
/frontend   → React + Vite
/docs       → informe, UML y material de la presentación
```

Paquetes del back (`com.nexus`): `controller`, `service`, `dao`, `model`, `patterns`.

**Entidades:** `Producto` · `Proveedor` · `PedidoReposicion` · `Movimiento`

Diagramas: [UML de clases](docs/uml-clases.md) · [Arquitectura en capas](docs/arquitectura.md)

---

## 🔌 API REST

| Método | Endpoint | Uso |
| :--- | :--- | :--- |
| GET | `/api/productos` | Tabla de stock |
| POST | `/api/ventas` | Registrar venta (dispara la cadena y el Observer) |
| GET | `/api/eventos` | Stream SSE con los eventos `stock` y `alerta` |
| PUT | `/api/estrategia` | Cambiar el perfil de selección de proveedor |
| POST | `/api/pedidos/one-click/{sku}` | Generar pedido y mensaje al proveedor |
| GET | `/api/pedidos` | Listar pedidos con su estado |
| POST | `/api/pedidos/{id}/avanzar` | Avanzar el estado de un pedido |

---

## 👥 Miembros del Equipo

| Nombre | Responsabilidad | Perfil de GitHub |
| :--- | :--- | :--- |
| Lucas Felice | Base del proyecto, Observer, Chain of Responsibility, `VentaService`, UML e integración del informe | [@LucasFelice10](https://github.com/LucasFelice10) |
| Nicolas Facendini | Persistencia: DAO JDBC, Abstract Factory, esquema y datos | [@nicofacen](https://github.com/nicofacen) |
| Brian Michelin | Strategy y lógica de reposición (ROP, stock de seguridad) | [@bmichelin](https://github.com/bmichelin) |
| Matias Ortiz | State, API REST, SSE y One-Click | [@matiasortiz123](https://github.com/matiasortiz123) |
| Lucia Pesalaccia | Front React, capturas, infografía y video | [@lpesalaccia](https://github.com/lpesalaccia) |

---

## 🛠️ Stack Tecnológico

**Back:** Java 17 · Maven · Spring Boot (capa REST) · JDBC · H2 · JUnit 5  
**Front:** React · Vite  
**Diseño y modelado:** StarUML · Mermaid  
**Herramientas:** Git & GitHub · IntelliJ IDEA

---

## ▶️ Cómo correrlo

```bash
# Back (desde /backend)
mvn spring-boot:run      # levanta la API en http://localhost:8080
mvn test                 # corre las pruebas unitarias

# Front (desde /frontend)
npm install
npm run dev
```

---

## 🌿 Flujo de trabajo

- Una rama por integrante: `feature/base-observer-chain`, `feature/dao-persistencia`, `feature/strategy-rop`, `feature/state-api`, `feature/frontend-react`.
- Pull Request obligatorio a `main`, con al menos un revisor distinto del autor.
- Commits chicos con prefijo: `feat:`, `fix:`, `test:`, `docs:`, `chore:`.
- Cada integrante commitea solo sus archivos.

---

## ⚠️ Limitaciones conocidas

- El mensaje al proveedor se arma y se simula: no hay integración real con una plataforma de mensajería.
- Los lead times salen del historial de los datos de prueba.
