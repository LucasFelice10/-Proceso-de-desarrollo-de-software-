import { USAR_MOCKS } from './config.js'
import * as mock from './mock.js'
import * as real from './real.js'

const api = USAR_MOCKS ? mock : real

export const {
  listarProductos,
  listarProveedores,
  obtenerProveedor,
  registrarVenta,
  cambiarEstrategia,
  generarOneClick,
  listarPedidos,
  avanzarPedido,
  simularDemora,
  suscribirEventos,
} = api

export { ErrorApi } from './ErrorApi.js'
