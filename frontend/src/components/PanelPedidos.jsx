import { useState } from 'react'
import { ETIQUETAS_ESTADO } from '../estadosPedido.js'
import { formatearFecha } from '../formato.js'
import MensajeError from './MensajeError.jsx'
import Panel from './Panel.jsx'
import './PanelPedidos.css'
import './Tabla.css'

// Activos primero y los más nuevos arriba; los recibidos, por fecha de recepción.
function ordenar(a, b) {
  const aRecibido = a.estado === 'RECIBIDO'
  const bRecibido = b.estado === 'RECIBIDO'
  if (aRecibido !== bRecibido) {
    return aRecibido ? 1 : -1
  }
  if (aRecibido) {
    return (b.fechaRecepcion ?? '').localeCompare(a.fechaRecepcion ?? '')
  }
  return b.id - a.id
}

function PanelPedidos({ datos, presentacion, className }) {
  const { pedidos, proveedores, seguidos, cargando, error, recargar, enCurso, errorAccion, avanzar, simularDemora } =
    datos
  const [ocultarHistorial, setOcultarHistorial] = useState(false)
  const sinHistorial = presentacion || ocultarHistorial

  const visibles = pedidos
    .filter((p) => !sinHistorial || p.estado !== 'RECIBIDO' || seguidos.has(p.id))
    .sort(ordenar)

  // En presentación el historial siempre está oculto, así que la opción no se muestra.
  const opciones = !presentacion && (
    <label className="pedidos__opcion">
      <input
        type="checkbox"
        checked={ocultarHistorial}
        onChange={(evento) => setOcultarHistorial(evento.target.checked)}
      />
      Ocultar historial de recibidos
    </label>
  )

  let contenido
  if (cargando && pedidos.length === 0) {
    contenido = <p className="panel__vacio">Cargando pedidos...</p>
  } else if (error && pedidos.length === 0) {
    contenido = <MensajeError mensaje={error} alReintentar={recargar} />
  } else if (visibles.length === 0) {
    contenido = <p className="panel__vacio">Sin pedidos para mostrar.</p>
  } else {
    contenido = (
      <table className="tabla">
        <thead>
          <tr>
            <th className="tabla__numero">N.º</th>
            <th>SKU</th>
            <th>Proveedor</th>
            <th className="tabla__numero">Cantidad</th>
            <th>Envío</th>
            <th>Estado</th>
            <th className="tabla__accion">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {visibles.map((pedido) => (
            <FilaPedido
              key={pedido.id}
              pedido={pedido}
              proveedor={proveedores[pedido.proveedorId] ?? `Proveedor #${pedido.proveedorId}`}
              ocupado={enCurso !== null}
              alAvanzar={avanzar}
              alSimularDemora={simularDemora}
            />
          ))}
        </tbody>
      </table>
    )
  }

  return (
    <Panel
      titulo="Pedidos de reposición"
      descripcion="Ciclo de cada pedido: Enviado, Demorado y Recibido"
      acciones={opciones}
      className={className}
    >
      <div className="pedidos">
        {errorAccion && <MensajeError mensaje={errorAccion} />}
        {error && pedidos.length > 0 && <MensajeError mensaje={error} alReintentar={recargar} />}
        {contenido}
      </div>
    </Panel>
  )
}

function FilaPedido({ pedido, proveedor, ocupado, alAvanzar, alSimularDemora }) {
  const { id, sku, cantidad, estado, fechaEnvio } = pedido
  const recibido = estado === 'RECIBIDO'
  return (
    <tr className={recibido ? 'tabla__fila--atenuada' : undefined}>
      <td className="tabla__numero">#{id}</td>
      <td className="tabla__codigo">{sku}</td>
      <td>{proveedor}</td>
      <td className="tabla__numero">{cantidad}</td>
      <td className="pedidos__fecha">{formatearFecha(fechaEnvio)}</td>
      <td>
        <span className={`estado-pedido estado-pedido--${estado.toLowerCase()}`}>
          {ETIQUETAS_ESTADO[estado] ?? estado}
        </span>
      </td>
      <td className="tabla__accion">
        <div className="pedidos__acciones">
          {estado === 'ENVIADO' && (
            <button
              type="button"
              className="boton boton--chico boton--secundario"
              disabled={ocupado}
              onClick={() => alSimularDemora(id)}
            >
              Simular demora
            </button>
          )}
          {(estado === 'ENVIADO' || estado === 'DEMORADO') && (
            <button
              type="button"
              className="boton boton--chico"
              disabled={ocupado}
              title="Pasa el pedido a Recibido y suma el stock"
              onClick={() => alAvanzar(id)}
            >
              Avanzar
            </button>
          )}
        </div>
      </td>
    </tr>
  )
}

export default PanelPedidos
