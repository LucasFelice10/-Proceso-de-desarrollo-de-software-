import Encabezado from './components/Encabezado.jsx'
import FormularioVenta from './components/FormularioVenta.jsx'
import ModalOneClick from './components/ModalOneClick.jsx'
import Panel from './components/Panel.jsx'
import PanelPedidos from './components/PanelPedidos.jsx'
import SelectorEstrategia from './components/SelectorEstrategia.jsx'
import TablaStock from './components/TablaStock.jsx'
import { useOneClick } from './hooks/useOneClick.js'
import { usePedidos } from './hooks/usePedidos.js'
import { useProductos } from './hooks/useProductos.js'
import './App.css'

function App() {
  const { productos, cargando, error, recargar, conectado, actualizaciones } = useProductos()
  const pedidos = usePedidos()
  const oneClick = useOneClick(pedidos.recargar)

  return (
    <div className="app">
      <Encabezado conectado={conectado} />
      <main className="contenido">
        <Panel
          titulo="Monitoreo de stock"
          descripcion="Stock actual y umbral crítico de cada producto"
          className="contenido__tabla"
        >
          <TablaStock
            productos={productos}
            cargando={cargando}
            error={error}
            alReintentar={recargar}
            actualizaciones={actualizaciones}
            alPedirOneClick={oneClick.pedir}
          />
        </Panel>

        <aside className="contenido__controles">
          <Panel titulo="Estrategia de proveedor" descripcion="Criterio para elegir proveedor en el One-Click">
            <SelectorEstrategia />
          </Panel>
          <Panel titulo="Registrar venta" descripcion="Descuenta stock y dispara la alerta si cruza el umbral">
            <FormularioVenta productos={productos} />
          </Panel>
        </aside>

        <PanelPedidos datos={pedidos} className="contenido__pedidos" />
      </main>

      {oneClick.solicitud && <ModalOneClick solicitud={oneClick.solicitud} alCerrar={oneClick.cerrar} />}
    </div>
  )
}

export default App
