<template>
  <v-container class="py-8">
    <h1 class="text-h4 font-weight-bold text-primary mb-2">
      {{ periodo === 'passados' ? 'Eventos já realizados' : 'Próximos eventos' }}
    </h1>
    <p class="text-body-1 mb-6">
      Oficinas, minicursos e rodas de conversa do Meninas Digitais. Escolha um evento e
      inscreva-se.
    </p>

    <v-btn-toggle v-model="periodo" mandatory color="primary" class="mb-6" divided>
      <v-btn value="futuros">Próximos</v-btn>
      <v-btn value="passados">Já realizados</v-btn>
    </v-btn-toggle>

    <v-alert v-if="erro" type="error" class="mb-4" variant="tonal">{{ erro }}</v-alert>
    <v-progress-linear v-if="carregando" indeterminate color="primary" class="mb-4" />

    <v-row v-if="!carregando && eventos.length === 0">
      <v-col>
        <v-alert type="info" variant="tonal">
          {{ periodo === 'passados' ? 'Nenhum evento passado encontrado.' : 'Ainda não há eventos abertos para inscrição.' }}
        </v-alert>
      </v-col>
    </v-row>

    <v-row>
      <v-col v-for="evento in eventos" :key="evento.id" cols="12" sm="6" md="4">
        <v-card class="h-100 d-flex flex-column" elevation="2">
          <v-card-title class="text-wrap">{{ evento.titulo }}</v-card-title>
          <v-card-subtitle>
            {{ formatarDataHora(evento.data) }} · {{ evento.local }}
          </v-card-subtitle>
          <v-card-text>
            <p class="mb-2">{{ resumo(evento.descricao) }}</p>
            <v-chip size="small" :color="evento.vagasDisponiveis > 0 ? 'success' : 'warning'">
              {{ evento.vagasDisponiveis }} vaga(s) de {{ evento.limiteVagas }}
            </v-chip>
          </v-card-text>
          <v-card-actions class="mt-auto">
            <v-btn color="primary" :to="{ name: 'evento-detalhe', params: { id: evento.id } }">
              Ver detalhes
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import http, { mensagemErro } from '@/api/http'
import { formatarDataHora } from '@/formatacao'

const periodo = ref('futuros')
const eventos = ref([])
const carregando = ref(false)
const erro = ref('')

function resumo(texto) {
  if (!texto) {
    return 'Clique para ver os detalhes e se inscrever.'
  }
  return texto.length > 140 ? `${texto.slice(0, 140)}…` : texto
}

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await http.get('/api/eventos', { params: { periodo: periodo.value } })
    eventos.value = data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível carregar os eventos.')
  } finally {
    carregando.value = false
  }
}

watch(periodo, carregar)
onMounted(carregar)
</script>
