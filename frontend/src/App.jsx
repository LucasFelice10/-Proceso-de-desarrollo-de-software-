import Encabezado from './components/Encabezado.jsx'
import ModalOneClick from './components/ModalOneClick.jsx'
import Panel from './components/Panel.jsx'
import SelectorEstrategia from './components/SelectorEstrategia.jsx'
import TablaStock from './components/TablaStock.jsx'
import { useOneClick } from './hooks/useOneClick.js'
import { useProductos } from './hooks/useProductos.js'
import './App.css'

function App() {
  const { productos, cargando, error, recargar, conectado, actualizaciones } = useProductos()
  const oneClick = useOneClick()

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

      {oneClick.solicitud && <ModalOneClick solicitud={oneClick.solicitud} alCerrar={oneClick.cerrar} />}
    </div>
  )
}

export default App
