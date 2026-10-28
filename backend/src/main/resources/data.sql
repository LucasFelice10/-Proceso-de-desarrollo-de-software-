-- Datos de prueba de la distribuidora. Las fechas son relativas a CURRENT_TIMESTAMP para que
-- la venta diaria y el historial de entregas tengan sentido cualquier día que se corra la demo.
-- MemoriaDAOFactory carga estos mismos datos (DatosDemo): si se cambia algo acá, cambiarlo allá.

INSERT INTO producto (sku, nombre, stock_actual, umbral_critico) VALUES
    ('CERV-X',   'Cerveza X 1 L',               220, 120),
    ('CERV-RUB', 'Cerveza rubia lata 473 ml',   260, 120),
    ('GAS-COLA', 'Gaseosa cola 2,25 L',         220, 100),
    ('GAS-LIMA', 'Gaseosa lima-limón 2,25 L',    90,  50),
    ('AGUA-SG',  'Agua mineral sin gas 2 L',    300, 130),
    ('AGUA-CG',  'Agua mineral con gas 1,5 L',   48,  40),
    ('LIC-VOD',  'Vodka 1 L',                    40,  20),
    ('LIC-WHI',  'Whisky 750 ml',                25,  15);

-- Ids 1 a 4 en este orden: el más barato y lento, el más caro y rápido, uno intermedio y uno irregular.
INSERT INTO proveedor (nombre, contacto) VALUES
    ('Distribuidora del Sur', '5491100000001'),
    ('Bebidas Express',       '5491100000002'),
    ('Mayorista Central',     '5491100000003'),
    ('Logística Norte',       '5491100000004');

-- Cerveza X la ofrecen los cuatro: Precio elige Sur, Velocidad elige Express y la Balanceada (60/40) elige Central.
INSERT INTO oferta_proveedor (proveedor_id, sku, precio, lead_time_dias) VALUES
    (1, 'CERV-X',     900.00, 6),
    (2, 'CERV-X',    1150.00, 1),
    (3, 'CERV-X',     990.00, 3),
    (4, 'CERV-X',     980.00, 5),
    (1, 'CERV-RUB',   750.00, 5),
    (2, 'CERV-RUB',   950.00, 1),
    (3, 'CERV-RUB',   820.00, 3),
    (1, 'GAS-COLA',  1400.00, 4),
    (2, 'GAS-COLA',  1750.00, 1),
    (4, 'GAS-COLA',  1500.00, 3),
    (1, 'GAS-LIMA',  1300.00, 4),
    (3, 'GAS-LIMA',  1450.00, 2),
    (2, 'AGUA-SG',    780.00, 1),
    (3, 'AGUA-SG',    650.00, 2),
    (4, 'AGUA-SG',    600.00, 4),
    (1, 'AGUA-CG',    560.00, 5),
    (2, 'AGUA-CG',    720.00, 1),
    (3, 'AGUA-CG',    610.00, 3),
    (2, 'LIC-VOD',   7800.00, 2),
    (3, 'LIC-VOD',   6900.00, 3),
    (4, 'LIC-VOD',   6500.00, 6),
    (1, 'LIC-WHI',  15500.00, 7),
    (2, 'LIC-WHI',  18900.00, 2),
    (3, 'LIC-WHI',  16800.00, 4);

-- Historial de entregas: pedidos ya recibidos. "hace" son los días desde el envío y "demora" los días
-- que tardó en llegar; las diferencias contra el lead time de la oferta son las que mide el desvío (σL).
INSERT INTO pedido_reposicion (sku, proveedor_id, cantidad, estado, fecha_creacion, fecha_envio, fecha_recepcion)
SELECT h.sku, h.proveedor_id, h.cantidad, 'RECIBIDO',
       DATEADD('DAY', -h.hace, CURRENT_TIMESTAMP),
       DATEADD('DAY', -h.hace, CURRENT_TIMESTAMP),
       DATEADD('DAY', h.demora - h.hace, CURRENT_TIMESTAMP)
FROM (VALUES
    ('CERV-X',   3, 150, 58, 3),
    ('CERV-X',   1, 180, 52, 8),
    ('CERV-X',   3, 150, 44, 4),
    ('CERV-X',   2, 120, 37, 1),
    ('CERV-X',   3, 150, 30, 3),
    ('CERV-X',   4, 160, 24, 7),
    ('CERV-X',   3, 150, 16, 5),
    ('CERV-X',   1, 180, 10, 7),
    ('CERV-RUB', 3, 200, 50, 3),
    ('CERV-RUB', 1, 240, 41, 6),
    ('CERV-RUB', 3, 200, 27, 4),
    ('CERV-RUB', 2, 150, 15, 1),
    ('GAS-COLA', 4, 180, 55, 3),
    ('GAS-COLA', 1, 200, 40, 5),
    ('GAS-COLA', 4, 180, 26, 4),
    ('GAS-COLA', 2, 120, 12, 2),
    ('GAS-LIMA', 1,  90, 48, 4),
    ('GAS-LIMA', 3,  80, 33, 2),
    ('GAS-LIMA', 1,  90, 19, 6),
    ('AGUA-SG',  3, 250, 45, 2),
    ('AGUA-SG',  4, 280, 36, 5),
    ('AGUA-SG',  3, 250, 22, 2),
    ('AGUA-SG',  2, 200,  9, 1),
    ('AGUA-CG',  3,  70, 47, 3),
    ('AGUA-CG',  1,  80, 31, 7),
    ('AGUA-CG',  3,  70, 18, 3),
    ('LIC-VOD',  3,  30, 60, 3),
    ('LIC-VOD',  4,  40, 39, 8),
    ('LIC-VOD',  2,  25, 20, 2),
    ('LIC-WHI',  3,  20, 58, 4),
    ('LIC-WHI',  1,  25, 34, 9),
    ('LIC-WHI',  2,  15, 14, 2)
) AS h (sku, proveedor_id, cantidad, hace, demora);

-- Cada recepción del historial entra al inventario como movimiento.
INSERT INTO movimiento (sku, tipo, cantidad, fecha)
SELECT sku, 'RECEPCION', cantidad, fecha_recepcion
FROM pedido_reposicion
WHERE estado = 'RECIBIDO';

-- Ventas de los últimos 30 días, una por día y producto: cantidad = base + MOD(dia * paso, rango).
INSERT INTO movimiento (sku, tipo, cantidad, fecha)
SELECT v.sku, 'VENTA', v.base + MOD(d.X * v.paso, v.rango), DATEADD('DAY', -d.X, CURRENT_TIMESTAMP)
FROM (VALUES
    ('CERV-X',   18,  7,  9),
    ('CERV-RUB', 30,  5, 11),
    ('GAS-COLA', 25,  3, 10),
    ('GAS-LIMA', 12,  7,  6),
    ('AGUA-SG',  35, 11, 13),
    ('AGUA-CG',  10,  3,  5),
    ('LIC-VOD',   3,  1,  4),
    ('LIC-WHI',   2,  2,  3)
) AS v (sku, base, paso, rango)
CROSS JOIN SYSTEM_RANGE(1, 30) AS d;
