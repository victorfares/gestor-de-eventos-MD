<template>
  <v-container class="py-8" style="max-width: 720px">
    <v-btn variant="text" color="primary" class="mb-4" to="/admin/eventos">Voltar</v-btn>
    <h1 class="text-h5 font-weight-bold text-primary mb-4">
      {{ editando ? 'Editar evento' : 'Novo evento' }}
    </h1>

    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>

    <v-card>
      <v-card-text>
        <v-form @submit.prevent="salvar">
          <v-text-field v-model="form.titulo" label="Título" maxlength="150" required />
          <v-textarea v-model="form.descricao" label="Descrição" maxlength="2000" rows="4" />
          <v-text-field v-model="form.data" label="Data e horário" type="datetime-local" required />
          <v-text-field v-model="form.local" label="Local" maxlength="200" required />
          <v-text-field
            v-model.number="form.limiteVagas"
            label="Limite de vagas"
            type="number"
            min="1"
            required
          />
          <v-btn type="submit" color="primary" :loading="enviando">Salvar</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { paraApiDataHora, paraCampoDataHora } from '@/formatacao'

const route = useRoute()
const router = useRouter()
const editando = computed(() => Boolean(route.params.id))
const enviando = ref(false)
const erro = ref('')
const form = reactive({
  titulo: '',
  descricao: '',
  data: '',
  local: '',
  limiteVagas: 20,
})

async function carregar() {
  if (!editando.value) {
    return
  }
  try {
    const { data } = await http.get(`/api/eventos/${route.params.id}`)
    form.titulo = data.titulo
    form.descricao = data.descricao || ''
    form.data = paraCampoDataHora(data.data)
    form.local = data.local
    form.limiteVagas = data.limiteVagas
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível carregar o evento.')
  }
}

async function salvar() {
  erro.value = ''
  enviando.value = true
  const corpo = {
    titulo: form.titulo,
    descricao: form.descricao || null,
    data: paraApiDataHora(form.data),
    local: form.local,
    limiteVagas: form.limiteVagas,
  }
  try {
    if (editando.value) {
      await http.put(`/api/eventos/${route.params.id}`, corpo)
    } else {
      await http.post('/api/eventos', corpo)
    }
    router.push({ name: 'admin-eventos' })
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível salvar o evento.')
  } finally {
    enviando.value = false
  }
}

onMounted(carregar)
</script>
