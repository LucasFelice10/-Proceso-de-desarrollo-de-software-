-- Esquema de Nexus: se recrea en cada arranque para que la demo empiece en un estado conocido.

DROP TABLE IF EXISTS movimiento;
DROP TABLE IF EXISTS pedido_reposicion;
DROP TABLE IF EXISTS oferta_proveedor;
DROP TABLE IF EXISTS proveedor;
DROP TABLE IF EXISTS producto;

CREATE TABLE producto (
    sku            VARCHAR(20)  PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    stock_actual   INT          NOT NULL CHECK (stock_actual >= 0),
    umbral_critico INT          NOT NULL CHECK (umbral_critico >= 0)
);

CREATE TABLE proveedor (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    -- Teléfono en formato internacional, sin '+', para armar el link wa.me
    contacto VARCHAR(30)  NOT NULL
);

-- Relación Producto N - N Proveedor: precio y lead time propios de cada proveedor.
CREATE TABLE oferta_proveedor (
    proveedor_id   BIGINT        NOT NULL REFERENCES proveedor (id),
    sku            VARCHAR(20)   NOT NULL REFERENCES producto (sku),
    precio         DECIMAL(12,2) NOT NULL CHECK (precio > 0),
    lead_time_dias INT           NOT NULL CHECK (lead_time_dias > 0),
    PRIMARY KEY (proveedor_id, sku)
);

-- Los pedidos recibidos son el historial de entregas: fecha_recepcion - fecha_envio es el lead time real.
CREATE TABLE pedido_reposicion (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku             VARCHAR(20) NOT NULL REFERENCES producto (sku),
    proveedor_id    BIGINT      NOT NULL REFERENCES proveedor (id),
    cantidad        INT         NOT NULL CHECK (cantidad > 0),
    estado          VARCHAR(20) NOT NULL DEFAULT 'GENERADO',
    fecha_creacion  TIMESTAMP   NOT NULL,
    fecha_envio     TIMESTAMP,
    fecha_recepcion TIMESTAMP
);

CREATE TABLE movimiento (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku      VARCHAR(20) NOT NULL REFERENCES producto (sku),
    tipo     VARCHAR(20) NOT NULL CHECK (tipo IN ('VENTA', 'RECEPCION')),
    cantidad INT         NOT NULL CHECK (cantidad > 0),
    fecha    TIMESTAMP   NOT NULL
);

CREATE INDEX idx_pedido_sku ON pedido_reposicion (sku);
CREATE INDEX idx_movimiento_sku_fecha ON movimiento (sku, fecha);
