const base = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export const LIMITE_FOTOS = 6
export const TAMANHO_MAXIMO_MB = 4
export const TIPOS_ACEITOS = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']

const TAMANHO_MAXIMO = TAMANHO_MAXIMO_MB * 1024 * 1024

export function urlFoto(eventoId, fotoId) {
  return `${base}/api/eventos/${eventoId}/fotos/${fotoId}`
}

export function formatarTamanho(bytes) {
  const opcoes = { maximumFractionDigits: 1, minimumFractionDigits: 1 }
  if (bytes < 1024 * 1024) {
    return `${Math.max(1, Math.round(bytes / 1024))} KB`
  }
  return `${(bytes / (1024 * 1024)).toLocaleString('pt-BR', opcoes)} MB`
}

// Mesmas regras do backend, para avisar antes de enviar o arquivo.
export function validarFotos(arquivos, quantidadeAtual = 0) {
  const erros = []
  if (quantidadeAtual + arquivos.length > LIMITE_FOTOS) {
    const restantes = Math.max(0, LIMITE_FOTOS - quantidadeAtual)
    erros.push(
      restantes === 0
        ? `Cada evento pode ter no máximo ${LIMITE_FOTOS} fotos. Remova uma foto antes de enviar outra.`
        : `Cada evento pode ter no máximo ${LIMITE_FOTOS} fotos. Você ainda pode enviar ${restantes}.`,
    )
  }
  for (const arquivo of arquivos) {
    if (arquivo.size > TAMANHO_MAXIMO) {
      erros.push(
        `A foto "${arquivo.name}" tem ${formatarTamanho(arquivo.size)} e passa do limite de ${TAMANHO_MAXIMO_MB} MB por foto. Reduza a imagem ou escolha outra.`,
      )
    } else if (!TIPOS_ACEITOS.includes(arquivo.type)) {
      erros.push(`A foto "${arquivo.name}" não está em um formato aceito. Envie JPG, PNG, WEBP ou GIF.`)
    }
  }
  return erros
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
