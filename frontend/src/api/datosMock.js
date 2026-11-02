// Copia de backend/src/main/resources/data.sql.

export const PRODUCTOS = [
  { sku: 'CERV-X', nombre: 'Cerveza X 1 L', stockActual: 220, umbralCritico: 120 },
  { sku: 'CERV-RUB', nombre: 'Cerveza rubia lata 473 ml', stockActual: 260, umbralCritico: 120 },
  { sku: 'GAS-COLA', nombre: 'Gaseosa cola 2,25 L', stockActual: 220, umbralCritico: 100 },
  { sku: 'GAS-LIMA', nombre: 'Gaseosa lima-limón 2,25 L', stockActual: 90, umbralCritico: 50 },
  { sku: 'AGUA-SG', nombre: 'Agua mineral sin gas 2 L', stockActual: 300, umbralCritico: 130 },
  { sku: 'AGUA-CG', nombre: 'Agua mineral con gas 1,5 L', stockActual: 48, umbralCritico: 40 },
  { sku: 'LIC-VOD', nombre: 'Vodka 1 L', stockActual: 40, umbralCritico: 20 },
  { sku: 'LIC-WHI', nombre: 'Whisky 750 ml', stockActual: 25, umbralCritico: 15 },
]

export const PROVEEDORES = [
  {
    id: 1,
    nombre: 'Distribuidora del Sur',
    contacto: '5491100000001',
    ofertas: [
      { sku: 'CERV-X', precio: 900, leadTimeDias: 6 },
      { sku: 'CERV-RUB', precio: 750, leadTimeDias: 5 },
      { sku: 'GAS-COLA', precio: 1400, leadTimeDias: 4 },
      { sku: 'GAS-LIMA', precio: 1300, leadTimeDias: 4 },
      { sku: 'AGUA-CG', precio: 560, leadTimeDias: 5 },
      { sku: 'LIC-WHI', precio: 15500, leadTimeDias: 7 },
    ],
  },
  {
    id: 2,
    nombre: 'Bebidas Express',
    contacto: '5491100000002',
    ofertas: [
      { sku: 'CERV-X', precio: 1150, leadTimeDias: 1 },
      { sku: 'CERV-RUB', precio: 950, leadTimeDias: 1 },
      { sku: 'GAS-COLA', precio: 1750, leadTimeDias: 1 },
      { sku: 'AGUA-SG', precio: 780, leadTimeDias: 1 },
      { sku: 'AGUA-CG', precio: 720, leadTimeDias: 1 },
      { sku: 'LIC-VOD', precio: 7800, leadTimeDias: 2 },
      { sku: 'LIC-WHI', precio: 18900, leadTimeDias: 2 },
    ],
  },
  {
    id: 3,
    nombre: 'Mayorista Central',
    contacto: '5491100000003',
    ofertas: [
      { sku: 'CERV-X', precio: 990, leadTimeDias: 3 },
      { sku: 'CERV-RUB', precio: 820, leadTimeDias: 3 },
      { sku: 'GAS-LIMA', precio: 1450, leadTimeDias: 2 },
      { sku: 'AGUA-SG', precio: 650, leadTimeDias: 2 },
      { sku: 'AGUA-CG', precio: 610, leadTimeDias: 3 },
      { sku: 'LIC-VOD', precio: 6900, leadTimeDias: 3 },
      { sku: 'LIC-WHI', precio: 16800, leadTimeDias: 4 },
    ],
  },
  {
    id: 4,
    nombre: 'Logística Norte',
    contacto: '5491100000004',
    ofertas: [
      { sku: 'CERV-X', precio: 980, leadTimeDias: 5 },
      { sku: 'GAS-COLA', precio: 1500, leadTimeDias: 3 },
      { sku: 'AGUA-SG', precio: 600, leadTimeDias: 4 },
      { sku: 'LIC-VOD', precio: 6500, leadTimeDias: 6 },
    ],
  },
]

// Pedidos ya recibidos: [sku, proveedorId, cantidad, días desde el envío, días que tardó].
export const HISTORIAL_ENTREGAS = [
  ['CERV-X', 3, 150, 58, 3],
  ['CERV-X', 1, 180, 52, 8],
  ['CERV-X', 3, 150, 44, 4],
  ['CERV-X', 2, 120, 37, 1],
  ['CERV-X', 3, 150, 30, 3],
  ['CERV-X', 4, 160, 24, 7],
  ['CERV-X', 3, 150, 16, 5],
  ['CERV-X', 1, 180, 10, 7],
  ['CERV-RUB', 3, 200, 50, 3],
  ['CERV-RUB', 1, 240, 41, 6],
  ['CERV-RUB', 3, 200, 27, 4],
  ['CERV-RUB', 2, 150, 15, 1],
  ['GAS-COLA', 4, 180, 55, 3],
  ['GAS-COLA', 1, 200, 40, 5],
  ['GAS-COLA', 4, 180, 26, 4],
  ['GAS-COLA', 2, 120, 12, 2],
  ['GAS-LIMA', 1, 90, 48, 4],
  ['GAS-LIMA', 3, 80, 33, 2],
  ['GAS-LIMA', 1, 90, 19, 6],
  ['AGUA-SG', 3, 250, 45, 2],
  ['AGUA-SG', 4, 280, 36, 5],
  ['AGUA-SG', 3, 250, 22, 2],
  ['AGUA-SG', 2, 200, 9, 1],
  ['AGUA-CG', 3, 70, 47, 3],
  ['AGUA-CG', 1, 80, 31, 7],
  ['AGUA-CG', 3, 70, 18, 3],
  ['LIC-VOD', 3, 30, 60, 3],
  ['LIC-VOD', 4, 40, 39, 8],
  ['LIC-VOD', 2, 25, 20, 2],
  ['LIC-WHI', 3, 20, 58, 4],
  ['LIC-WHI', 1, 25, 34, 9],
  ['LIC-WHI', 2, 15, 14, 2],
]

// Venta diaria y ROP que calcula el back con estos datos (docs/strategy-rop.md). El mock no los recalcula.
export const REFERENCIA_ROP = {
  'CERV-X': { ventaDiaria: 22.1, puntoDePedido: 194 },
  'CERV-RUB': { ventaDiaria: 35.27, puntoDePedido: 245 },
  'GAS-COLA': { ventaDiaria: 29.5, puntoDePedido: 166 },
  'GAS-LIMA': { ventaDiaria: 14.5, puntoDePedido: 106 },
  'AGUA-SG': { ventaDiaria: 41.27, puntoDePedido: 221 },
  'AGUA-CG': { ventaDiaria: 12, puntoDePedido: 98 },
  'LIC-VOD': { ventaDiaria: 4.5, puntoDePedido: 43 },
  'LIC-WHI': { ventaDiaria: 3, puntoDePedido: 33 },
}
