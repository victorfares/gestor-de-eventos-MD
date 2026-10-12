<template>
  <v-container class="py-8">
    <div class="d-flex flex-wrap align-center mb-4">
      <h1 class="text-h5 font-weight-bold text-primary mr-4">Eventos</h1>
      <v-spacer />
      <v-select
        v-model="periodo"
        :items="opcoesPeriodo"
        density="compact"
        hide-details
        style="max-width: 220px"
        class="mr-3"
      />
      <v-btn color="primary" to="/admin/eventos/novo">Novo evento</v-btn>
    </div>

    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
    <v-alert v-if="aviso" type="success" variant="tonal" class="mb-4">{{ aviso }}</v-alert>

    <v-data-table
      :headers="colunas"
      :items="eventos"
      :loading="carregando"
      item-value="id"
      :items-per-page="25"
      class="bg-surface"
    >
      <template #item.data="{ item }">{{ formatarDataHora(item.data) }}</template>
      <template #item.vagas="{ item }">
        {{ item.vagasDisponiveis }} / {{ item.limiteVagas }}
      </template>
      <template #item.acoes="{ item }">
        <v-btn size="small" variant="text" :to="{ name: 'admin-inscritas', params: { id: item.id } }">
          Inscritas
        </v-btn>
        <v-btn size="small" variant="text" :to="{ name: 'admin-evento-editar', params: { id: item.id } }">
          Editar
        </v-btn>
        <v-btn size="small" variant="text" color="error" @click="confirmarExclusao(item)">
          Excluir
        </v-btn>
      </template>
    </v-data-table>

    <v-dialog v-model="dialogo" max-width="420">
      <v-card>
        <v-card-title>Excluir evento</v-card-title>
        <v-card-text>
          Excluir "{{ alvo?.titulo }}" também remove as inscrições. Essa ação não pode ser desfeita.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="dialogo = false">Cancelar</v-btn>
          <v-btn color="error" :loading="excluindo" @click="excluir">Excluir</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import http, { mensagemErro } from '@/api/http'
import { formatarDataHora } from '@/formatacao'

const periodo = ref(null)
const opcoesPeriodo = [
  { title: 'Todos', value: null },
  { title: 'Próximos', value: 'futuros' },
  { title: 'Já realizados', value: 'passados' },
]
const colunas = [
  { title: 'Título', key: 'titulo' },
  { title: 'Data', key: 'data' },
  { title: 'Local', key: 'local' },
  { title: 'Vagas', key: 'vagas' },
  { title: 'Ações', key: 'acoes', sortable: false },
]
const eventos = ref([])
const carregando = ref(false)
const erro = ref('')
const aviso = ref('')
const dialogo = ref(false)
const alvo = ref(null)
const excluindo = ref(false)

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const params = periodo.value ? { periodo: periodo.value } : {}
    const { data } = await http.get('/api/eventos', { params })
    eventos.value = data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível listar os eventos.')
  } finally {
    carregando.value = false
  }
}

function confirmarExclusao(item) {
  alvo.value = item
  dialogo.value = true
}

async function excluir() {
  excluindo.value = true
  try {
    await http.delete(`/api/eventos/${alvo.value.id}`)
    aviso.value = 'Evento excluído.'
    dialogo.value = false
    await carregar()
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível excluir o evento.')
  } finally {
    excluindo.value = false
  }
}

watch(periodo, carregar)
onMounted(carregar)
</script>
