const base = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export function urlFoto(eventoId, fotoId) {
  return `${base}/api/eventos/${eventoId}/fotos/${fotoId}`
}

export async function enviarFotos(http, eventoId, arquivos) {
  if (!arquivos?.length) {
    return null
  }
  const dados = new FormData()
  for (const arquivo of arquivos) {
    dados.append('fotos', arquivo)
  }
  const { data } = await http.post(`/api/eventos/${eventoId}/fotos`, dados, {
    transformRequest: [(corpo, headers) => {
      delete headers['Content-Type']
      return corpo
    }],
  })
  return data
}
