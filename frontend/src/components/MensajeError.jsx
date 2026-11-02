import './MensajeError.css'

function MensajeError({ mensaje, alReintentar }) {
  return (
    <div className="mensaje-error" role="alert">
      <p>{mensaje}</p>
      {alReintentar && (
        <button type="button" className="boton boton--secundario" onClick={alReintentar}>
          Reintentar
        </button>
      )}
    </div>
  )
}

export default MensajeError
