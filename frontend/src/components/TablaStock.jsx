import MensajeError from './MensajeError.jsx'
import './Tabla.css'

function TablaStock({ productos, cargando, error, alReintentar }) {
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
        {productos.map((producto) => (
          <tr key={producto.sku}>
            <td className="tabla__codigo">{producto.sku}</td>
            <td>{producto.nombre}</td>
            <td className="tabla__numero tabla__destacado">{producto.stockActual}</td>
            <td className="tabla__numero">{producto.umbralCritico}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default TablaStock
