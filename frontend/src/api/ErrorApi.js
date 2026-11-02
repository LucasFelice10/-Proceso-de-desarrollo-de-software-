export class ErrorApi extends Error {
  constructor(mensaje, codigo) {
    super(mensaje)
    this.name = 'ErrorApi'
    this.codigo = codigo
  }
}

export function mensajeGenerico(codigo) {
  return `Error del servidor (código ${codigo})`
}
