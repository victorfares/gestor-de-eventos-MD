<template>
  <v-container class="py-8">
    <h1 class="text-h5 font-weight-bold text-primary mb-4">Membros da equipe</h1>
    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
    <v-alert v-if="aviso" type="success" variant="tonal" class="mb-4">{{ aviso }}</v-alert>

    <v-row>
      <v-col cols="12" md="7">
        <v-data-table
          :headers="colunas"
          :items="membros"
          :loading="carregando"
          item-value="id"
          class="bg-surface"
        />
      </v-col>
      <v-col cols="12" md="5">
        <v-card>
          <v-card-title>Novo membro</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="criar">
              <v-text-field v-model="form.nome" label="Nome" required />
              <v-text-field v-model="form.email" label="E-mail" type="email" required />
              <v-text-field v-model="form.senha" label="Senha (mínimo 8 caracteres)" type="password" required />
              <v-btn type="submit" color="primary" :loading="enviando">Cadastrar</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import http, { mensagemErro } from '@/api/http'

const membros = ref([])
const carregando = ref(false)
const enviando = ref(false)
const erro = ref('')
const aviso = ref('')
const form = reactive({ nome: '', email: '', senha: '' })
const colunas = [
  { title: 'Nome', key: 'nome' },
  { title: 'E-mail', key: 'email' },
]

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await http.get('/api/usuarios')
    membros.value = data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível listar os membros.')
  } finally {
    carregando.value = false
  }
}

async function criar() {
  erro.value = ''
  aviso.value = ''
  enviando.value = true
  try {
    await http.post('/api/usuarios', { ...form })
    aviso.value = 'Membro cadastrado.'
    form.nome = ''
    form.email = ''
    form.senha = ''
    await carregar()
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível cadastrar o membro.')
  } finally {
    enviando.value = false
  }
}

onMounted(carregar)
</script>
