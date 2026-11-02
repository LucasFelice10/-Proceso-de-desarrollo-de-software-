import { useCallback, useEffect, useState } from 'react'
import { listarProductos, suscribirEventos } from '../api'

// Mismo orden que el ORDER BY sku del back.
const porSku = (a, b) => (a.sku < b.sku ? -1 : a.sku > b.sku ? 1 : 0)

export function useProductos() {
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [intento, setIntento] = useState(0)
  const [conectado, setConectado] = useState(null)
  // Cuántos eventos recibió cada SKU: la tabla lo usa para repetir la animación del cambio.
  const [actualizaciones, setActualizaciones] = useState({})

  useEffect(() => {
    let vigente = true
    listarProductos()
      .then((lista) => vigente && setProductos([...lista].sort(porSku)))
      .catch((e) => vigente && setError(e.message))
      .finally(() => vigente && setCargando(false))
    return () => {
      vigente = false
    }
  }, [intento])

  const recargar = useCallback(() => {
    setCargando(true)
    setError(null)
    setIntento((anterior) => anterior + 1)
  }, [])

  useEffect(() => {
    let caido = false
    const aplicar = (datos) =>
      setProductos((lista) => lista.map((p) => (p.sku === datos.sku ? { ...p, ...datos } : p)))

    return suscribirEventos({
      alCambiarStock: (datos) => {
        aplicar(datos)
        setActualizaciones((anterior) => ({ ...anterior, [datos.sku]: (anterior[datos.sku] ?? 0) + 1 }))
      },
      alAlertar: aplicar,
      alCambiarConexion: (activa) => {
        // Los eventos que se perdieron mientras estaba caído se recuperan recargando la lista.
        if (activa && caido) {
          recargar()
        }
        caido = !activa
        setConectado(activa)
      },
    })
  }, [recargar])

  return { productos, cargando, error, recargar, conectado, actualizaciones }
}
