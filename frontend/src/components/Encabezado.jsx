import './Encabezado.css'

function Encabezado() {
  return (
    <header className="encabezado">
      <div className="encabezado__marca">
        <svg className="encabezado__logo" viewBox="0 0 64 64" aria-hidden="true">
          <rect width="64" height="64" rx="14" />
          <path d="M20 46V18l24 28V18" />
        </svg>
        <h1 className="encabezado__titulo">
          Nexus Software <span className="encabezado__subtitulo">- Gestión de Stock</span>
        </h1>
      </div>
    </header>
  )
}

export default Encabezado
