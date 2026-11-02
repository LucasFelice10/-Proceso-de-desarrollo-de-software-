import './IndicadorConexion.css'

const TEXTOS = {
  conectando: 'Conectando',
  activo: 'En vivo',
  caido: 'Sin conexión',
}

function IndicadorConexion({ conectado }) {
  const estado = conectado === null ? 'conectando' : conectado ? 'activo' : 'caido'
  return (
    <span className={`indicador indicador--${estado}`} role="status">
      <span className="indicador__punto" aria-hidden="true" />
      {TEXTOS[estado]}
    </span>
  )
}

export default IndicadorConexion
