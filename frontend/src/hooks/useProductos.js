import { useCallback, useEffect, useState } from 'react'
import { listarProductos } from '../api'

// Mismo orden que el ORDER BY sku del back.
const porSku = (a, b) => (a.sku < b.sku ? -1 : a.sku > b.sku ? 1 : 0)

export function useProductos() {
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [intento, setIntento] = useState(0)

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

  return { productos, cargando, error, recargar }
}
