import { ETIQUETAS_ESTADO } from '../estadosPedido.js'
import MensajeError from './MensajeError.jsx'
import Modal from './Modal.jsx'
import './ModalOneClick.css'

function ModalOneClick({ solicitud, alCerrar }) {
  const { producto, cargando, resultado, error } = solicitud

  const acciones = (
    <>
      <button type="button" className="boton boton--secundario" onClick={alCerrar}>
        Cerrar
      </button>
      {resultado && (
        <a className="boton" href={resultado.link} target="_blank" rel="noopener noreferrer">
          Abrir en WhatsApp
        </a>
      )}
    </>
  )

  return (
    <Modal
      titulo="Pedido One-Click"
      subtitulo={`${producto.sku} · ${producto.nombre}`}
      alCerrar={alCerrar}
      acciones={acciones}
    >
      {cargando && <p className="panel__vacio">Generando pedido...</p>}
      {error && <MensajeError mensaje={error} />}
      {resultado && (
        <>
          <dl className="one-click__datos">
            <div>
              <dt>Proveedor elegido</dt>
              <dd>{resultado.proveedor}</dd>
            </div>
            <div>
              <dt>Cantidad</dt>
              <dd>{resultado.pedido.cantidad} unidades</dd>
            </div>
            <div>
              <dt>Pedido</dt>
              <dd>
                #{resultado.pedido.id} · {ETIQUETAS_ESTADO[resultado.pedido.estado] ?? resultado.pedido.estado}
              </dd>
            </div>
          </dl>
          <p className="one-click__etiqueta">Mensaje al proveedor</p>
          <blockquote className="one-click__mensaje">{resultado.mensaje}</blockquote>
          <p className="one-click__nota">El envío se simula: el link abre WhatsApp con el mensaje ya escrito.</p>
        </>
      )}
    </Modal>
  )
}

export default ModalOneClick
