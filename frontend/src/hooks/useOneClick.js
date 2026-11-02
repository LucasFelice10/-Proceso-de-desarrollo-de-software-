import { useCallback, useRef, useState } from 'react'
import { generarOneClick } from '../api'

export function useOneClick(alGenerar) {
  const [solicitud, setSolicitud] = useState(null)
  // Si el modal se cerró antes de que llegue la respuesta, la respuesta se descarta.
  const ultimaSolicitud = useRef(0)

  const pedir = useCallback(async (producto) => {
    const id = ++ultimaSolicitud.current
    setSolicitud({ producto, cargando: true })
    try {
      const resultado = await generarOneClick(producto.sku)
      alGenerar?.()
      if (ultimaSolicitud.current === id) {
        setSolicitud({ producto, cargando: false, resultado })
      }
    } catch (e) {
      if (ultimaSolicitud.current === id) {
        setSolicitud({ producto, cargando: false, error: e.message })
      }
    }
  }, [alGenerar])

  const cerrar = useCallback(() => {
    ultimaSolicitud.current++
    setSolicitud(null)
  }, [])

  return { solicitud, pedir, cerrar }
}
