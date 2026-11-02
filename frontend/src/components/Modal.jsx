import { useEffect, useRef } from 'react'
import './Modal.css'

function Modal({ titulo, subtitulo, alCerrar, acciones, children }) {
  const dialogo = useRef(null)

  useEffect(() => {
    const elemento = dialogo.current
    elemento.showModal()
    return () => elemento.close()
  }, [])

  const cancelar = (evento) => {
    evento.preventDefault()
    alCerrar()
  }

  // El contenido ocupa todo el dialog: un click que llega al dialog en sí fue sobre el fondo.
  const clickEnFondo = (evento) => {
    if (evento.target === dialogo.current) {
      alCerrar()
    }
  }

  return (
    <dialog ref={dialogo} className="modal" onCancel={cancelar} onClick={clickEnFondo}>
      <div className="modal__contenido">
        <header className="modal__encabezado">
          <div>
            <h2 className="modal__titulo">{titulo}</h2>
            {subtitulo && <p className="modal__subtitulo">{subtitulo}</p>}
          </div>
          <button type="button" className="modal__cerrar" aria-label="Cerrar" onClick={alCerrar}>
            ×
          </button>
        </header>
        <div className="modal__cuerpo">{children}</div>
        {acciones && <footer className="modal__pie">{acciones}</footer>}
      </div>
    </dialog>
  )
}

export default Modal
