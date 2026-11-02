import './Panel.css'

function Panel({ titulo, descripcion, acciones, className = '', children }) {
  return (
    <section className={`panel ${className}`}>
      <header className="panel__encabezado">
        <div>
          <h2 className="panel__titulo">{titulo}</h2>
          {descripcion && <p className="panel__descripcion">{descripcion}</p>}
        </div>
        {acciones && <div className="panel__acciones">{acciones}</div>}
      </header>
      <div className="panel__cuerpo">{children}</div>
    </section>
  )
}

export default Panel
