<template>
  <v-container class="py-8">
    <v-btn variant="text" color="primary" class="mb-4" to="/admin/eventos">Voltar</v-btn>
    <div class="d-flex flex-wrap align-center mb-4">
      <div>
        <h1 class="text-h5 font-weight-bold text-primary">Inscritas</h1>
        <p v-if="evento" class="mb-0">{{ evento.titulo }} · {{ formatarDataHora(evento.data) }}</p>
      </div>
      <v-spacer />
      <v-btn color="secondary" :loading="exportando" @click="exportar">Exportar CSV</v-btn>
    </div>

    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>

    <v-data-table
      :headers="colunas"
      :items="inscritas"
      :loading="carregando"
      item-value="id"
      :items-per-page="50"
      class="bg-surface"
    >
      <template #item.dataInscricao="{ item }">
        {{ formatarDataHora(item.dataInscricao) }}
      </template>
    </v-data-table>
  </v-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { formatarDataHora } from '@/formatacao'

const route = useRoute()
const evento = ref(null)
const inscritas = ref([])
const carregando = ref(false)
const exportando = ref(false)
const erro = ref('')
const colunas = [
  { title: 'Nome', key: 'nome' },
  { title: 'E-mail', key: 'email' },
  { title: 'Idade/série', key: 'idadeOuSerie' },
  { title: 'Inscrição', key: 'dataInscricao' },
]

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const [eventoRes, listaRes] = await Promise.all([
      http.get(`/api/eventos/${route.params.id}`),
      http.get(`/api/eventos/${route.params.id}/inscricoes`),
    ])
    evento.value = eventoRes.data
    inscritas.value = listaRes.data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível carregar as inscritas.')
  } finally {
    carregando.value = false
  }
}

async function exportar() {
  exportando.value = true
  erro.value = ''
  try {
    const resposta = await http.get(`/api/eventos/${route.params.id}/inscricoes/exportar`, {
      responseType: 'blob',
    })
    const nome = nomeArquivo(resposta.headers['content-disposition'])
    const url = URL.createObjectURL(resposta.data)
    const link = document.createElement('a')
    link.href = url
    link.download = nome
    link.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível exportar o CSV.')
  } finally {
    exportando.value = false
  }
}

function nomeArquivo(cabecalho) {
  const padrao = /filename\*?=(?:UTF-8'')?"?([^";]+)/i
  const encontrado = cabecalho && cabecalho.match(padrao)
  return encontrado ? decodeURIComponent(encontrado[1].replace(/"/g, '')) : 'inscritas.csv'
}

onMounted(carregar)
</script>
