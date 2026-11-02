import { useState } from 'react'
import { cambiarEstrategia } from '../api'
import MensajeError from './MensajeError.jsx'
import './SelectorEstrategia.css'

const PERFILES = [
  { perfil: 'PRECIO', nombre: 'Precio', criterio: 'Elige la oferta de menor precio' },
  { perfil: 'VELOCIDAD', nombre: 'Velocidad', criterio: 'Elige la oferta de menor lead time' },
  { perfil: 'BALANCEADA', nombre: 'Balanceada', criterio: '60% precio y 40% velocidad' },
]

// El back arranca con esta estrategia y no tiene un GET para consultarla.
const PERFIL_INICIAL = 'BALANCEADA'

function SelectorEstrategia() {
  const [activa, setActiva] = useState(PERFIL_INICIAL)
  const [pendiente, setPendiente] = useState(null)
  const [error, setError] = useState(null)

  const cambiar = async (perfil) => {
    if (perfil === activa || pendiente) {
      return
    }
    setPendiente(perfil)
    setError(null)
    try {
      const respuesta = await cambiarEstrategia(perfil)
      setActiva(respuesta.perfil)
    } catch (e) {
      setError(e.message)
    } finally {
      setPendiente(null)
    }
  }

  return (
    <div className="estrategia">
      <fieldset className="estrategia__opciones" disabled={pendiente !== null}>
        <legend className="visualmente-oculto">Perfil de selección de proveedor</legend>
        {PERFILES.map(({ perfil, nombre, criterio }) => {
          const seleccionada = perfil === activa
          return (
            <label
              key={perfil}
              className={`estrategia__opcion${seleccionada ? ' estrategia__opcion--activa' : ''}`}
            >
              <input
                type="radio"
                name="estrategia"
                value={perfil}
                checked={seleccionada}
                onChange={() => cambiar(perfil)}
              />
              <span className="estrategia__nombre">{nombre}</span>
              <span className="estrategia__criterio">{criterio}</span>
              {seleccionada && <span className="estrategia__estado">Activa</span>}
              {pendiente === perfil && <span className="estrategia__estado">Aplicando...</span>}
            </label>
          )
        })}
      </fieldset>
      {error && <MensajeError mensaje={error} />}
    </div>
  )
}

export default SelectorEstrategia
