import Encabezado from './components/Encabezado.jsx'
import Panel from './components/Panel.jsx'
import TablaStock from './components/TablaStock.jsx'
import { useProductos } from './hooks/useProductos.js'
import './App.css'

function App() {
  const { productos, cargando, error, recargar } = useProductos()

  return (
    <div className="app">
      <Encabezado />
      <main className="contenido">
        <Panel
          titulo="Monitoreo de stock"
          descripcion="Stock actual y umbral crítico de cada producto"
          className="contenido__tabla"
        >
          <TablaStock productos={productos} cargando={cargando} error={error} alReintentar={recargar} />
        </Panel>

        <aside className="contenido__controles">
          <Panel titulo="Estrategia de proveedor" descripcion="Criterio para elegir proveedor en el One-Click">
            <p className="panel__vacio">Sin perfiles cargados.</p>
          </Panel>
          <Panel titulo="Registrar venta" descripcion="Descuenta stock y dispara la alerta si cruza el umbral">
            <p className="panel__vacio">Sin productos para vender.</p>
          </Panel>
        </aside>

        <Panel
          titulo="Pedidos de reposición"
          descripcion="Ciclo de cada pedido: Enviado, Demorado y Recibido"
          className="contenido__pedidos"
        >
          <p className="panel__vacio">Sin pedidos para mostrar.</p>
        </Panel>
      </main>
    </div>
  )
}

export default App
