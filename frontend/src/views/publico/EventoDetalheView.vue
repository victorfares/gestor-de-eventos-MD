<template>
  <v-container class="py-8">
    <v-btn variant="text" color="primary" class="mb-4" to="/">Voltar aos eventos</v-btn>

    <v-alert v-if="erroEvento" type="error" class="mb-4" variant="tonal">{{ erroEvento }}</v-alert>
    <v-progress-linear v-if="carregando" indeterminate color="primary" class="mb-4" />

    <v-row v-if="evento">
      <v-col cols="12" md="7">
        <v-card>
          <v-card-title class="text-h5 text-wrap">{{ evento.titulo }}</v-card-title>
          <v-card-text>
            <p class="text-body-1">{{ evento.descricao || 'Sem descrição.' }}</p>
            <v-list density="compact">
              <v-list-item prepend-icon="mdi-calendar" :title="formatarDataHora(evento.data)" subtitle="Data e horário" />
              <v-list-item prepend-icon="mdi-map-marker" :title="evento.local" subtitle="Local" />
              <v-list-item
                prepend-icon="mdi-account-group"
                :title="`${evento.vagasDisponiveis} vaga(s) disponível(is) de ${evento.limiteVagas}`"
              />
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="5">
        <v-card>
          <v-card-title>Quero participar</v-card-title>
          <v-card-text>
            <v-alert v-if="sucesso" type="success" variant="tonal" class="mb-4">
              Inscrição confirmada, {{ sucesso.nome }}! Guardamos seu e-mail {{ sucesso.email }}.
            </v-alert>
            <v-alert v-if="erroInscricao" type="error" variant="tonal" class="mb-4">
              {{ erroInscricao }}
            </v-alert>
            <v-alert v-if="encerrado" type="info" variant="tonal">
              Este evento já aconteceu. As inscrições estão encerradas.
            </v-alert>
            <v-alert v-else-if="evento.vagasDisponiveis <= 0" type="warning" variant="tonal">
              Não há vagas disponíveis neste evento.
            </v-alert>
            <v-form v-else-if="!sucesso" @submit.prevent="inscrever">
              <v-text-field v-model="form.nome" label="Nome completo" required />
              <v-text-field v-model="form.email" label="E-mail" type="email" required />
              <v-text-field v-model="form.idadeOuSerie" label="Idade ou série" required />
              <v-btn type="submit" color="primary" :loading="enviando" block>
                Confirmar inscrição
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { formatarDataHora } from '@/formatacao'

const route = useRoute()
const evento = ref(null)
const carregando = ref(false)
const enviando = ref(false)
const erroEvento = ref('')
const erroInscricao = ref('')
const sucesso = ref(null)
const form = reactive({ nome: '', email: '', idadeOuSerie: '' })

const encerrado = computed(() => evento.value && new Date(evento.value.data) < new Date())

async function carregar() {
  carregando.value = true
  erroEvento.value = ''
  try {
    const { data } = await http.get(`/api/eventos/${route.params.id}`)
    evento.value = data
  } catch (e) {
    erroEvento.value = mensagemErro(e, 'Evento não encontrado.')
  } finally {
    carregando.value = false
  }
}

async function inscrever() {
  erroInscricao.value = ''
  enviando.value = true
  try {
    const { data } = await http.post('/api/inscricoes', {
      nome: form.nome,
      email: form.email,
      idadeOuSerie: form.idadeOuSerie,
      eventoId: route.params.id,
    })
    sucesso.value = data
    evento.value.vagasDisponiveis = Math.max(0, evento.value.vagasDisponiveis - 1)
  } catch (e) {
    erroInscricao.value = mensagemErro(e, 'Não foi possível concluir a inscrição.')
  } finally {
    enviando.value = false
  }
}

onMounted(carregar)
</script>
