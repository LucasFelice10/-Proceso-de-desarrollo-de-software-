import { VENTAS_AUTOMATICAS_MOCK } from './config.js'
import { ErrorApi, mensajeGenerico } from './ErrorApi.js'
import { HISTORIAL_ENTREGAS, PRODUCTOS, PROVEEDORES, REFERENCIA_ROP } from './datosMock.js'

const DEMORA_RED_MS = 150
const INTERVALO_VENTAS_MS = 2000
const MS_POR_DIA = 24 * 60 * 60 * 1000
const DIAS_DEMORA = 2
const DIAS_COBERTURA = 7
const PERFILES = ['PRECIO', 'VELOCIDAD', 'BALANCEADA']

// Mismo formato que el LocalDateTime del back: ISO sin zona horaria.
function fechaLocal(fecha) {
  const desfase = fecha.getTimezoneOffset() * 60 * 1000
  return new Date(fecha.getTime() - desfase).toISOString().slice(0, 19)
}

const haceDias = (dias) => new Date(Date.now() - dias * MS_POR_DIA)

const productos = structuredClone(PRODUCTOS)
const proveedores = structuredClone(PROVEEDORES)
const pedidos = HISTORIAL_ENTREGAS.map(([sku, proveedorId, cantidad, hace, demora], indice) => ({
  id: indice + 1,
  sku,
  proveedorId,
  cantidad,
  estado: 'RECIBIDO',
  fechaCreacion: fechaLocal(haceDias(hace)),
  fechaEnvio: fechaLocal(haceDias(hace)),
  fechaRecepcion: fechaLocal(haceDias(hace - demora)),
}))
let siguienteId = pedidos.length + 1
let estrategia = 'BALANCEADA'
const suscriptores = new Set()
let temporizadorVentas = null

// Devuelve copias para que los componentes no modifiquen el estado del mock.
async function simularRed(operacion) {
  await new Promise((resolver) => setTimeout(resolver, DEMORA_RED_MS))
  return structuredClone(operacion())
}

function buscarProducto(sku, codigo) {
  const producto = productos.find((p) => p.sku === sku)
  if (!producto) {
    throw new ErrorApi(`Producto inexistente: ${sku}`, codigo)
  }
  return producto
}

function buscarPedido(id) {
  const pedido = pedidos.find((p) => p.id === Number(id))
  if (!pedido) {
    throw new ErrorApi(`Pedido inexistente: ${id}`, 404)
  }
  return pedido
}

function notificar(producto) {
  const { sku, nombre, stockActual, umbralCritico } = producto
  for (const suscriptor of suscriptores) {
    suscriptor.alCambiarStock?.({ sku, nombre, stockActual, umbralCritico })
    if (stockActual <= umbralCritico) {
      suscriptor.alAlertar?.({ sku, nombre, stockActual, umbralCritico })
    }
  }
}

function vender(producto, cantidad) {
  const stockAnterior = producto.stockActual
  producto.stockActual -= cantidad
  notificar(producto)
  const umbralCruzado =
    stockAnterior > producto.umbralCritico && producto.stockActual <= producto.umbralCritico
  return { sku: producto.sku, cantidad, stockActual: producto.stockActual, umbralCruzado, alertaDisparada: umbralCruzado }
}

// Vende entre el 2% y el 6% del stock de un producto al azar: baja rápido sin llegar a cero.
function venderAlAzar() {
  const producto = productos[Math.floor(Math.random() * productos.length)]
  const cantidad = Math.max(1, Math.round(producto.stockActual * (0.02 + Math.random() * 0.04)))
  if (producto.stockActual >= cantidad) {
    vender(producto, cantidad)
  }
}

function normalizar(valor, valores) {
  const minimo = Math.min(...valores)
  const maximo = Math.max(...valores)
  return maximo === minimo ? 0 : (valor - minimo) / (maximo - minimo)
}

const COMPARADORES = {
  PRECIO: (a, b) => a.precio - b.precio || a.leadTimeDias - b.leadTimeDias,
  VELOCIDAD: (a, b) => a.leadTimeDias - b.leadTimeDias || a.precio - b.precio,
  BALANCEADA: (a, b) => a.puntaje - b.puntaje,
}

function elegirProveedor(sku) {
  const candidatos = proveedores
    .map((proveedor) => ({ proveedor, ...proveedor.ofertas.find((o) => o.sku === sku) }))
    .filter((candidato) => candidato.sku)
  if (candidatos.length === 0) {
    throw new ErrorApi(`Ningún proveedor ofrece ${sku}`, 404)
  }
  const precios = candidatos.map((c) => c.precio)
  const leadTimes = candidatos.map((c) => c.leadTimeDias)
  for (const candidato of candidatos) {
    candidato.puntaje =
      0.6 * normalizar(candidato.precio, precios) + 0.4 * normalizar(candidato.leadTimeDias, leadTimes)
  }
  return candidatos.sort(COMPARADORES[estrategia])[0].proveedor
}

function cantidadSugerida(producto) {
  const { ventaDiaria, puntoDePedido } = REFERENCIA_ROP[producto.sku]
  const enCamino = pedidos
    .filter((p) => p.sku === producto.sku && p.estado !== 'RECIBIDO')
    .reduce((total, p) => total + p.cantidad, 0)
  const posicion = producto.stockActual + enCamino
  if (posicion > puntoDePedido) {
    return 0
  }
  return puntoDePedido + Math.round(ventaDiaria * DIAS_COBERTURA) - posicion
}

function actualizarDemoras() {
  const limite = Date.now() - DIAS_DEMORA * MS_POR_DIA
  for (const pedido of pedidos) {
    if (pedido.estado === 'ENVIADO' && new Date(pedido.fechaEnvio).getTime() < limite) {
      pedido.estado = 'DEMORADO'
    }
  }
}

export const listarProductos = () => simularRed(() => productos)

export const listarProveedores = () => simularRed(() => proveedores)

export const obtenerProveedor = (id) =>
  simularRed(() => {
    const proveedor = proveedores.find((p) => p.id === Number(id))
    if (!proveedor) {
      throw new ErrorApi(mensajeGenerico(404), 404)
    }
    return proveedor
  })

export const registrarVenta = (sku, cantidad) =>
  simularRed(() => {
    if (!sku || !sku.trim()) {
      throw new ErrorApi('El sku es obligatorio', 400)
    }
    if (cantidad <= 0) {
      throw new ErrorApi('La cantidad debe ser mayor a cero', 400)
    }
    const producto = buscarProducto(sku, 400)
    if (producto.stockActual < cantidad) {
      throw new ErrorApi(
        `Stock insuficiente de ${producto.nombre}: hay ${producto.stockActual} y se piden ${cantidad}`,
        400,
      )
    }
    return vender(producto, cantidad)
  })

export const cambiarEstrategia = (perfil) =>
  simularRed(() => {
    const normalizado = (perfil ?? '').trim().toUpperCase()
    if (!PERFILES.includes(normalizado)) {
      throw new ErrorApi('Perfil inválido: debe ser PRECIO, VELOCIDAD o BALANCEADA', 400)
    }
    estrategia = normalizado
    return { perfil: normalizado }
  })

export const generarOneClick = (sku) =>
  simularRed(() => {
    const producto = buscarProducto(sku, 404)
    const cantidad = cantidadSugerida(producto)
    if (cantidad === 0) {
      throw new ErrorApi(`No hace falta reponer ${sku}: el stock más lo ya pedido supera el punto de pedido`, 409)
    }
    const proveedor = elegirProveedor(sku)
    const ahora = fechaLocal(new Date())
    const pedido = {
      id: siguienteId++,
      sku,
      proveedorId: proveedor.id,
      cantidad,
      estado: 'ENVIADO',
      fechaCreacion: ahora,
      fechaEnvio: ahora,
      fechaRecepcion: null,
    }
    pedidos.push(pedido)
    const mensaje =
      `Hola ${proveedor.nombre}, necesitamos reponer stock. ` +
      `Pedido #${pedido.id}: SKU ${sku} (${producto.nombre}) - ${cantidad} unidades. ` +
      'Por favor confirmar disponibilidad y fecha de entrega. Gracias, Nexus Software.'
    const link = `https://wa.me/${proveedor.contacto.replace(/\D/g, '')}?text=${encodeURIComponent(mensaje)}`
    return { pedido, proveedor: proveedor.nombre, mensaje, link }
  })

export const listarPedidos = () =>
  simularRed(() => {
    actualizarDemoras()
    return pedidos
  })

export const avanzarPedido = (id) =>
  simularRed(() => {
    const pedido = buscarPedido(id)
    if (pedido.estado === 'RECIBIDO') {
      throw new ErrorApi(`El pedido ${pedido.id} ya fue recibido`, 409)
    }
    if (pedido.estado === 'GENERADO') {
      pedido.estado = 'ENVIADO'
      pedido.fechaEnvio = fechaLocal(new Date())
      return pedido
    }
    pedido.estado = 'RECIBIDO'
    pedido.fechaRecepcion = fechaLocal(new Date())
    const producto = buscarProducto(pedido.sku, 404)
    producto.stockActual += pedido.cantidad
    notificar(producto)
    return pedido
  })

export const simularDemora = (id) =>
  simularRed(() => {
    const pedido = buscarPedido(id)
    if (pedido.estado !== 'ENVIADO') {
      throw new ErrorApi(
        `Solo un pedido ENVIADO puede demorarse, el pedido ${pedido.id} está ${pedido.estado}`,
        409,
      )
    }
    pedido.fechaEnvio = fechaLocal(haceDias(DIAS_DEMORA + 1))
    pedido.estado = 'DEMORADO'
    return pedido
  })

export function suscribirEventos(manejadores) {
  suscriptores.add(manejadores)
  setTimeout(() => {
    if (suscriptores.has(manejadores)) {
      manejadores.alCambiarConexion?.(true)
    }
  }, DEMORA_RED_MS)
  if (VENTAS_AUTOMATICAS_MOCK && !temporizadorVentas) {
    temporizadorVentas = setInterval(venderAlAzar, INTERVALO_VENTAS_MS)
  }
  return () => {
    suscriptores.delete(manejadores)
    if (suscriptores.size === 0) {
      clearInterval(temporizadorVentas)
      temporizadorVentas = null
    }
  }
}
