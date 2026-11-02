import { useState } from 'react'
import { registrarVenta } from '../api'
import MensajeError from './MensajeError.jsx'
import './FormularioVenta.css'

function FormularioVenta({ productos }) {
  const [sku, setSku] = useState('')
  const [cantidad, setCantidad] = useState('1')
  const [enviando, setEnviando] = useState(false)
  const [resultado, setResultado] = useState(null)
  const [error, setError] = useState(null)

  if (productos.length === 0) {
    return <p className="panel__vacio">Sin productos para vender.</p>
  }

  const skuElegido = sku || productos[0].sku

  const enviar = async (evento) => {
    evento.preventDefault()
    setResultado(null)
    setError(null)
    const unidades = Number(cantidad)
    if (!Number.isInteger(unidades) || unidades <= 0) {
      setError('La cantidad debe ser un número entero mayor a cero')
      return
    }
    setEnviando(true)
    try {
      setResultado(await registrarVenta(skuElegido, unidades))
    } catch (e) {
      setError(e.message)
    } finally {
      setEnviando(false)
    }
  }

  const productoVendido = resultado && productos.find((p) => p.sku === resultado.sku)

  return (
    <form className="venta" onSubmit={enviar}>
      <label className="venta__campo">
        <span className="venta__etiqueta">Producto</span>
        <select value={skuElegido} onChange={(evento) => setSku(evento.target.value)}>
          {productos.map((producto) => (
            <option key={producto.sku} value={producto.sku}>
              {producto.sku} · {producto.nombre}
            </option>
          ))}
        </select>
      </label>

      <label className="venta__campo">
        <span className="venta__etiqueta">Cantidad</span>
        <input
          type="number"
          min="1"
          step="1"
          required
          value={cantidad}
          onChange={(evento) => setCantidad(evento.target.value)}
        />
      </label>

      <button type="submit" className="boton" disabled={enviando}>
        {enviando ? 'Registrando...' : 'Registrar venta'}
      </button>

      {error && <MensajeError mensaje={error} />}
      {resultado && (
        <div
          className={`venta__resultado${resultado.alertaDisparada ? ' venta__resultado--alerta' : ''}`}
          role="status"
        >
          <p>
            Venta de {resultado.cantidad} u. de {productoVendido?.nombre ?? resultado.sku}. Quedan{' '}
            <strong>{resultado.stockActual}</strong> unidades.
          </p>
          <p className="venta__alerta">
            {resultado.alertaDisparada ? 'Cruzó el umbral crítico: alerta disparada.' : 'Sin alerta.'}
          </p>
        </div>
      )}
    </form>
  )
}

export default FormularioVenta
