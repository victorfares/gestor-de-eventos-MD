import axios from 'axios'
import { obterToken, encerrarSessao } from '@/auth/sessao'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = obterToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const status = erro.response?.status
    if (status === 401 && window.location.pathname.startsWith('/admin')) {
      encerrarSessao()
      window.location.assign('/login')
    }
    return Promise.reject(erro)
  },
)

export function mensagemErro(erro, fallback = 'Não foi possível concluir a operação.') {
  const dados = erro.response?.data
  if (!dados) {
    return fallback
  }
  if (Array.isArray(dados.errors) && dados.errors.length) {
    return dados.errors.map((item) => item.mensagem).join(' ')
  }
  return dados.message || fallback
}

export default http
