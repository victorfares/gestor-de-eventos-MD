<template>
  <v-container class="py-8 linha-do-tempo">
    <div class="d-flex flex-wrap align-center justify-space-between ga-4 mb-8">
      <div>
        <h1 class="text-h4 font-weight-bold text-primary mb-2">Eventos</h1>
        <p class="text-body-1 mb-0">
          Oficinas, minicursos e rodas de conversa do Meninas Digitais.
        </p>
      </div>
      <v-btn color="primary" size="large" variant="tonal" @click="mostrarPassados = !mostrarPassados">
        {{ mostrarPassados ? 'Ver os próximos' : 'Ver os passados' }}
      </v-btn>
    </div>

    <v-alert v-if="erro" type="error" class="mb-4" variant="tonal">{{ erro }}</v-alert>
    <v-progress-linear v-if="carregando" indeterminate color="primary" class="mb-4" />
    <v-alert v-if="!carregando && grupos.length === 0" type="info" variant="tonal">
      {{ mostrarPassados ? 'Nenhum evento passado.' : 'Nenhum evento futuro no momento.' }}
    </v-alert>

    <section v-for="grupo in grupos" :key="grupo.rotulo" class="mes-grupo">
      <h2 class="mes-titulo">{{ grupo.rotulo }}</h2>
      <article v-for="evento in grupo.eventos" :key="evento.id" class="osesp-linha">
        <div class="osesp-dia">
          <span>{{ partes(evento.data).semana }}</span>
          <strong>{{ partes(evento.data).dia }}</strong>
          <span>{{ partes(evento.data).mes }}</span>
        </div>

        <router-link class="osesp-foto" :to="{ name: 'evento-detalhe', params: { id: evento.id } }">
          <img
            v-if="evento.fotos?.length"
            :src="urlFoto(evento.id, evento.fotos[0])"
            :alt="evento.titulo"
          />
          <span v-else>{{ evento.titulo }}</span>
        </router-link>

        <div class="osesp-texto">
          <router-link class="osesp-titulo" :to="{ name: 'evento-detalhe', params: { id: evento.id } }">
            {{ evento.titulo }}
          </router-link>
          <p>{{ resumo(evento.descricao) }}</p>
          <dl class="osesp-dados">
            <div>
              <dt>Local</dt>
              <dd>{{ evento.local }}</dd>
            </div>
            <div>
              <dt>Data</dt>
              <dd>{{ partes(evento.data).dataLonga }}</dd>
            </div>
            <div>
              <dt>Horário</dt>
              <dd>{{ partes(evento.data).hora }}</dd>
            </div>
            <div>
              <dt>Vagas</dt>
              <dd>{{ evento.vagasDisponiveis }} de {{ evento.limiteVagas }}</dd>
            </div>
          </dl>
        </div>
      </article>
    </section>
  </v-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import http, { mensagemErro } from '@/api/http'
import { urlFoto } from '@/api/fotos'
import { partesData } from '@/formatacao'

const eventos = ref([])
const carregando = ref(false)
const erro = ref('')
const mostrarPassados = ref(false)

const grupos = computed(() => {
  const agora = new Date()
  const ordenados = eventos.value
    .filter((evento) => (mostrarPassados.value ? new Date(evento.data) < agora : new Date(evento.data) >= agora))
    .sort((a, b) =>
      mostrarPassados.value
        ? new Date(b.data) - new Date(a.data)
        : new Date(a.data) - new Date(b.data),
    )
  const lista = []
  for (const evento of ordenados) {
    const data = new Date(evento.data)
    const bruto = data.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' })
    const rotulo = bruto.charAt(0).toUpperCase() + bruto.slice(1)
    const existente = lista.find((grupo) => grupo.rotulo === rotulo)
    if (existente) {
      existente.eventos.push(evento)
    } else {
      lista.push({ rotulo, eventos: [evento] })
    }
  }
  return lista
})

function partes(valor) {
  return partesData(valor)
}

function resumo(texto) {
  if (!texto) return 'Toque para ver os detalhes e se inscrever.'
  return texto.length > 160 ? `${texto.slice(0, 160)}…` : texto
}

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const { data } = await http.get('/api/eventos')
    eventos.value = data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível carregar os eventos.')
  } finally {
    carregando.value = false
  }
}

onMounted(carregar)
</script>
