// Las fechas llegan como LocalDateTime sin zona (con o sin fracción de segundo): se leen tal cual.
const FECHA_ISO = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/

export function formatearFecha(fecha) {
  const partes = fecha ? FECHA_ISO.exec(fecha) : null
  if (!partes) {
    return '-'
  }
  const [, , mes, dia, hora, minutos] = partes
  return `${dia}/${mes} ${hora}:${minutos}`
}
