<template>
  <div>
    <v-alert v-if="erroEvento" type="error" class="ma-6" variant="tonal">{{ erroEvento }}</v-alert>
    <v-progress-linear v-if="carregando" indeterminate color="primary" />

    <template v-if="evento">
      <section class="osesp-hero" :class="{ expandida: fotoAberta }">
        <img v-if="fotoAtual" :src="urlFoto(evento.id, fotoAtual)" :alt="evento.titulo" />
        <div v-else class="osesp-hero-vazio">
          <h1>{{ evento.titulo }}</h1>
        </div>
        <button
          v-if="fotoAtual"
          class="expandir-foto"
          type="button"
          :aria-label="fotoAberta ? 'Recolher foto' : 'Ampliar foto'"
          @click="fotoAberta = !fotoAberta"
        >
          <v-icon :icon="fotoAberta ? 'mdi-chevron-up' : 'mdi-chevron-down'" />
        </button>
      </section>

      <div v-if="evento.fotos?.length > 1" class="osesp-miniaturas">
        <button
          v-for="foto in evento.fotos"
          :key="foto"
          type="button"
          :class="{ ativa: foto === fotoAtual }"
          @click="fotoAtual = foto"
        >
          <img :src="urlFoto(evento.id, foto)" :alt="evento.titulo" />
        </button>
      </div>

      <v-container class="evento-miolo py-8">
        <v-btn variant="text" color="primary" class="mb-4 px-0" to="/">Voltar aos eventos</v-btn>
        <h1 class="osesp-pagina-titulo">{{ evento.titulo }}</h1>
        <div class="divisor" />

        <dl class="osesp-dados-centrais">
          <div>
            <dt>Local</dt>
            <dd>{{ evento.local }}</dd>
          </div>
          <div>
            <dt>Data</dt>
            <dd>{{ partes.dataLonga }}</dd>
          </div>
          <div>
            <dt>Horário</dt>
            <dd>{{ partes.hora }}</dd>
          </div>
          <div>
            <dt>Vagas</dt>
            <dd>{{ evento.vagasDisponiveis }} de {{ evento.limiteVagas }}</dd>
          </div>
        </dl>
        <div class="divisor" />

        <p class="osesp-pagina-texto">{{ evento.descricao || 'Este evento ainda não tem uma descrição.' }}</p>
        <div class="divisor" />

        <div class="evento-inscricao">
          <v-btn class="btn-participar" color="primary" size="x-large" block @click="inscricaoAberta = !inscricaoAberta">
            {{ inscricaoAberta ? 'Fechar inscrição' : 'Quero participar' }}
          </v-btn>
          <v-expand-transition>
          <div v-show="inscricaoAberta">
          <p class="inscricao-apoio">Nome completo, e-mail e idade ou série. A vaga fica guardada na hora.</p>
          <v-alert v-if="sucesso" type="success" variant="tonal" class="mb-4">
            Inscrição confirmada, {{ sucesso.nome }}! Guardamos seu e-mail {{ sucesso.email }}.
          </v-alert>
          <v-alert v-if="erroInscricao" type="error" variant="tonal" class="mb-4">{{ erroInscricao }}</v-alert>
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
            <v-btn type="submit" color="secondary" :loading="enviando" block size="large">
              Confirmar inscrição
            </v-btn>
          </v-form>
          </div>
          </v-expand-transition>
        </div>
      </v-container>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { urlFoto } from '@/api/fotos'
import { partesData } from '@/formatacao'

const route = useRoute()
const evento = ref(null)
const carregando = ref(false)
const enviando = ref(false)
const erroEvento = ref('')
const erroInscricao = ref('')
const sucesso = ref(null)
const fotoAtual = ref(null)
const fotoAberta = ref(false)
const inscricaoAberta = ref(false)
const form = reactive({ nome: '', email: '', idadeOuSerie: '' })

const encerrado = computed(() => evento.value && new Date(evento.value.data) < new Date())
const partes = computed(() => (evento.value ? partesData(evento.value.data) : {}))

async function carregar() {
  carregando.value = true
  erroEvento.value = ''
  try {
    const { data } = await http.get(`/api/eventos/${route.params.id}`)
    evento.value = data
    fotoAtual.value = data.fotos?.[0] || null
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
