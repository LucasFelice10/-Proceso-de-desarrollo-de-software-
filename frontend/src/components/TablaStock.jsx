import MensajeError from './MensajeError.jsx'
import './Tabla.css'

function TablaStock({ productos, cargando, error, alReintentar, actualizaciones }) {
  if (error) {
    return <MensajeError mensaje={error} alReintentar={alReintentar} />
  }
  if (cargando && productos.length === 0) {
    return <p className="panel__vacio">Cargando productos...</p>
  }
  if (productos.length === 0) {
    return <p className="panel__vacio">Sin productos para mostrar.</p>
  }

  return (
    <table className="tabla">
      <thead>
        <tr>
          <th>SKU</th>
          <th>Producto</th>
          <th className="tabla__numero">Stock actual</th>
          <th className="tabla__numero">Umbral crítico</th>
        </tr>
      </thead>
      <tbody>
        {productos.map((producto) => {
          const enAlerta = producto.stockActual <= producto.umbralCritico
          const cambios = actualizaciones[producto.sku] ?? 0
          return (
            <tr key={producto.sku} className={enAlerta ? 'tabla__fila--alerta' : undefined}>
              <td className="tabla__codigo">{producto.sku}</td>
              <td>
                {producto.nombre}
                {enAlerta && <span className="tabla__etiqueta-alerta">Bajo umbral</span>}
              </td>
              {/* La key cambia con cada evento y vuelve a montar la celda para repetir la animación. */}
              <td
                key={cambios}
                className={`tabla__numero tabla__destacado${cambios > 0 ? ' tabla__celda--actualizada' : ''}`}
              >
                {producto.stockActual}
              </td>
              <td className="tabla__numero">{producto.umbralCritico}</td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}

export default TablaStock
