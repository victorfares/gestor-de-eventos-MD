export function partesData(valor) {
  const data = new Date(valor)
  const semana = data.toLocaleDateString('pt-BR', { weekday: 'short' }).replace('.', '')
  const mes = data.toLocaleDateString('pt-BR', { month: 'short' }).replace('.', '')
  return {
    semana: semana.slice(0, 3).toUpperCase(),
    dia: String(data.getDate()),
    mes: mes.charAt(0).toUpperCase() + mes.slice(1),
    hora: data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' }),
    dataLonga: data.toLocaleDateString('pt-BR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }),
  }
}

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
