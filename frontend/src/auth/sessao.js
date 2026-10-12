import { computed, reactive } from 'vue'

const CHAVE_TOKEN = 'md.token'
const CHAVE_USUARIO = 'md.usuario'

const estado = reactive({
  token: localStorage.getItem(CHAVE_TOKEN),
  usuario: lerUsuario(),
})

function lerUsuario() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE_USUARIO) || 'null')
  } catch {
    return null
  }
}

export const usuarioLogado = computed(() => estado.usuario)
export const autenticada = computed(() => Boolean(estado.token))

export function obterToken() {
  return estado.token
}

export function iniciarSessao({ token, usuario }) {
  estado.token = token
  estado.usuario = usuario
  localStorage.setItem(CHAVE_TOKEN, token)
  localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario))
}

export function encerrarSessao() {
  estado.token = null
  estado.usuario = null
  localStorage.removeItem(CHAVE_TOKEN)
  localStorage.removeItem(CHAVE_USUARIO)
}
