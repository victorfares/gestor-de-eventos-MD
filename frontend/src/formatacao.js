export function formatarDataHora(valor) {
  if (!valor) {
    return '—'
  }
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(valor))
}

export function paraCampoDataHora(valor) {
  if (!valor) {
    return ''
  }
  return String(valor).slice(0, 16)
}

export function paraApiDataHora(valor) {
  if (!valor) {
    return null
  }
  return valor.length === 16 ? `${valor}:00` : valor
}
