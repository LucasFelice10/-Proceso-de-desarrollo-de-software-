import { URL_BASE } from './config.js'
import { ErrorApi, mensajeGenerico } from './ErrorApi.js'

async function pedir(ruta, { metodo = 'GET', cuerpo } = {}) {
  let respuesta
  try {
    respuesta = await fetch(URL_BASE + ruta, {
      method: metodo,
      headers: cuerpo === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo),
    })
  } catch {
    throw new ErrorApi('No se pudo conectar con el servidor', 0)
  }
  const datos = await leerJson(respuesta)
  if (!respuesta.ok) {
    throw new ErrorApi(mensajeDeError(datos, respuesta.status), respuesta.status)
  }
  return datos
}

async function leerJson(respuesta) {
  const texto = await respuesta.text()
  if (!texto) {
    return null
  }
  try {
    return JSON.parse(texto)
  } catch {
    return null
  }
}

// Los errores del negocio llegan como {"error": "..."}; los propios de Spring traen además status.
function mensajeDeError(datos, codigo) {
  if (datos && typeof datos.error === 'string' && datos.status === undefined) {
    return datos.error
  }
  return mensajeGenerico(codigo)
}

export const listarProductos = () => pedir('/api/productos')

export const listarProveedores = () => pedir('/api/proveedores')

export const obtenerProveedor = (id) => pedir(`/api/proveedores/${id}`)

export const registrarVenta = (sku, cantidad) =>
  pedir('/api/ventas', { metodo: 'POST', cuerpo: { sku, cantidad } })

export const cambiarEstrategia = (perfil) =>
  pedir('/api/estrategia', { metodo: 'PUT', cuerpo: { perfil } })

export const generarOneClick = (sku) =>
  pedir(`/api/pedidos/one-click/${encodeURIComponent(sku)}`, { metodo: 'POST' })

export const listarPedidos = () => pedir('/api/pedidos')

export const avanzarPedido = (id) => pedir(`/api/pedidos/${id}/avanzar`, { metodo: 'POST' })

export const simularDemora = (id) => pedir(`/api/pedidos/${id}/simular-demora`, { metodo: 'POST' })

export function suscribirEventos({ alCambiarStock, alAlertar, alCambiarConexion }) {
  const fuente = new EventSource(`${URL_BASE}/api/eventos`)
  const leer = (manejador) => (evento) => manejador?.(JSON.parse(evento.data))

  fuente.addEventListener('stock', leer(alCambiarStock))
  fuente.addEventListener('alerta', leer(alAlertar))
  fuente.addEventListener('open', () => alCambiarConexion?.(true))
  // EventSource reintenta la conexión por su cuenta; mientras tanto se informa como caída.
  fuente.addEventListener('error', () => alCambiarConexion?.(false))

  return () => fuente.close()
}
