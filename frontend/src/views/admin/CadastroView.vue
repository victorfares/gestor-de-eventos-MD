<template>
  <v-container class="py-12 d-flex justify-center">
    <v-card max-width="460" width="100%" class="pa-2">
      <v-card-title class="text-h5">Quero fazer parte da equipe</v-card-title>
      <v-card-subtitle class="text-wrap">
        Preencha seus dados. Um membro da equipe vai analisar e aprovar seu acesso ao painel.
      </v-card-subtitle>
      <v-card-text>
        <v-alert v-if="enviado" type="success" variant="tonal" class="mb-4">
          Solicitação enviada! Assim que um membro da equipe aprovar seu cadastro, você poderá entrar
          com o e-mail e a senha que escolheu.
        </v-alert>
        <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>

        <v-form v-if="!enviado" ref="formulario" @submit.prevent="enviar">
          <v-text-field
            v-model="form.nome"
            label="Nome completo"
            maxlength="150"
            :rules="[regras.nomeCompleto]"
            required
          />
          <v-text-field
            v-model="form.email"
            label="E-mail"
            type="email"
            maxlength="150"
            :rules="[regras.email]"
            required
          />
          <v-text-field
            v-model="form.senha"
            label="Senha (mínimo 8 caracteres)"
            :type="mostrarSenha ? 'text' : 'password'"
            :append-inner-icon="mostrarSenha ? 'mdi-eye-off' : 'mdi-eye'"
            @click:append-inner="mostrarSenha = !mostrarSenha"
            maxlength="100"
            :rules="[regras.senha]"
            required
          />
          <v-text-field
            v-model="form.confirmacao"
            label="Confirme a senha"
            :type="mostrarSenha ? 'text' : 'password'"
            maxlength="100"
            :rules="[regras.confirmacao]"
            required
          />
          <v-btn type="submit" color="primary" :loading="enviando" block>Enviar solicitação</v-btn>
        </v-form>

        <div class="text-center mt-4 text-body-2">
          Já tem acesso?
          <router-link :to="{ name: 'login' }">Entrar no painel</router-link>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { reactive, ref } from 'vue'
import http, { mensagemErro } from '@/api/http'

const formulario = ref(null)
const enviando = ref(false)
const enviado = ref(false)
const mostrarSenha = ref(false)
const erro = ref('')
const form = reactive({ nome: '', email: '', senha: '', confirmacao: '' })

const regras = {
  nomeCompleto: (v) => /^\s*\S+(\s+\S+)+\s*$/.test(v || '') || 'Informe seu nome completo (nome e sobrenome).',
  email: (v) => /^\S+@\S+\.\S+$/.test(v || '') || 'Informe um e-mail válido.',
  senha: (v) => (v || '').length >= 8 || 'A senha deve ter pelo menos 8 caracteres.',
  confirmacao: (v) => v === form.senha || 'As senhas não conferem.',
}

async function enviar() {
  erro.value = ''
  const { valid } = await formulario.value.validate()
  if (!valid) {
    return
  }
  enviando.value = true
  try {
    await http.post('/api/auth/cadastro', {
      nome: form.nome,
      email: form.email,
      senha: form.senha,
    })
    enviado.value = true
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível enviar a solicitação.')
  } finally {
    enviando.value = false
  }
}
</script>
