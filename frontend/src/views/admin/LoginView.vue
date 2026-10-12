<template>
  <v-container class="py-12 d-flex justify-center">
    <v-card max-width="420" width="100%" class="pa-2">
      <v-card-title class="text-h5">Área da equipe</v-card-title>
      <v-card-subtitle>Entre com seu e-mail de membro do projeto.</v-card-subtitle>
      <v-card-text>
        <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
        <v-form @submit.prevent="entrar">
          <v-text-field v-model="email" label="E-mail" type="email" required />
          <v-text-field
            v-model="senha"
            label="Senha"
            :type="mostrarSenha ? 'text' : 'password'"
            :append-inner-icon="mostrarSenha ? 'mdi-eye-off' : 'mdi-eye'"
            @click:append-inner="mostrarSenha = !mostrarSenha"
            required
          />
          <v-btn type="submit" color="primary" :loading="enviando" block>Entrar</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { iniciarSessao } from '@/auth/sessao'

const route = useRoute()
const router = useRouter()
const email = ref('')
const senha = ref('')
const mostrarSenha = ref(false)
const enviando = ref(false)
const erro = ref('')

async function entrar() {
  erro.value = ''
  enviando.value = true
  try {
    const { data } = await http.post('/api/auth/login', {
      email: email.value,
      senha: senha.value,
    })
    iniciarSessao({ token: data.token, usuario: data.usuario })
    router.push(route.query.redirect || { name: 'admin-eventos' })
  } catch (e) {
    erro.value = mensagemErro(e, 'E-mail ou senha inválidos.')
  } finally {
    enviando.value = false
  }
}
</script>
