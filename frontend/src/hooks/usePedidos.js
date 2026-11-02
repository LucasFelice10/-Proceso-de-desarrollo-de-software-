import { useCallback, useEffect, useState } from 'react'
import { avanzarPedido, listarPedidos, listarProveedores, simularDemora as simularDemoraPedido } from '../api'

export function usePedidos() {
  const [pedidos, setPedidos] = useState([])
  const [proveedores, setProveedores] = useState({})
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [intento, setIntento] = useState(0)
  // Pedidos que se vieron activos en esta sesión: siguen visibles aunque se oculte el historial.
  const [seguidos, setSeguidos] = useState(() => new Set())
  const [enCurso, setEnCurso] = useState(null)
  const [errorAccion, setErrorAccion] = useState(null)

  useEffect(() => {
    let vigente = true
    Promise.all([listarPedidos(), listarProveedores()])
      .then(([listaPedidos, listaProveedores]) => {
        if (!vigente) {
          return
        }
        setPedidos(listaPedidos)
        setProveedores(Object.fromEntries(listaProveedores.map((p) => [p.id, p.nombre])))
        setSeguidos((anterior) => {
          const activos = listaPedidos.filter((p) => p.estado !== 'RECIBIDO').map((p) => p.id)
          return activos.every((id) => anterior.has(id)) ? anterior : new Set([...anterior, ...activos])
        })
        setError(null)
      })
      .catch((e) => vigente && setError(e.message))
      .finally(() => vigente && setCargando(false))
    return () => {
      vigente = false
    }
  }, [intento])

  const recargar = useCallback(() => setIntento((anterior) => anterior + 1), [])

  const ejecutar = useCallback(
    async (accion, id) => {
      setEnCurso(id)
      setErrorAccion(null)
      try {
        await accion(id)
      } catch (e) {
        setErrorAccion(`Pedido #${id}: ${e.message}`)
      } finally {
        setEnCurso(null)
        recargar()
      }
    },
    [recargar],
  )

  const avanzar = useCallback((id) => ejecutar(avanzarPedido, id), [ejecutar])
  const simularDemora = useCallback((id) => ejecutar(simularDemoraPedido, id), [ejecutar])

  return {
    pedidos,
    proveedores,
    seguidos,
    cargando,
    error,
    recargar,
    enCurso,
    errorAccion,
    avanzar,
    simularDemora,
  }
}
