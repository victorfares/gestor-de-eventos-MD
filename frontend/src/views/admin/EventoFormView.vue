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
          <v-file-input
            v-model="novasFotos"
            label="Fotos do evento"
            accept="image/png,image/jpeg,image/webp,image/gif"
            prepend-icon="mdi-camera"
            multiple
            :error-messages="errosFotos"
            :hint="`Até ${LIMITE_FOTOS} fotos, JPG, PNG, WEBP ou GIF, no máximo ${TAMANHO_MAXIMO_MB} MB cada.`"
            persistent-hint
          >
            <template #selection="{ fileNames }">
              {{ listaNovasFotos.map((f, i) => `${fileNames[i]} (${formatarTamanho(f.size)})`).join(', ') }}
            </template>
          </v-file-input>
          <div v-if="fotosAtuais.length" class="d-flex flex-wrap ga-3 my-4">
            <div v-for="foto in fotosAtuais" :key="foto" class="text-center">
              <img :src="urlFoto(route.params.id, foto)" alt="" class="miniatura-foto ativa" />
              <v-btn size="x-small" variant="text" color="error" @click="removerFoto(foto)">Remover</v-btn>
            </div>
          </div>
          <v-btn type="submit" color="primary" :loading="enviando" class="mt-4">Salvar</v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http, { mensagemErro } from '@/api/http'
import { LIMITE_FOTOS, TAMANHO_MAXIMO_MB, enviarFotos, formatarTamanho, urlFoto, validarFotos } from '@/api/fotos'
import { paraApiDataHora, paraCampoDataHora } from '@/formatacao'

const route = useRoute()
const router = useRouter()
const editando = computed(() => Boolean(route.params.id))
const enviando = ref(false)
const erro = ref('')
const novasFotos = ref([])
const fotosAtuais = ref([])
const listaNovasFotos = computed(() =>
  Array.isArray(novasFotos.value) ? novasFotos.value : [novasFotos.value].filter(Boolean),
)
const errosFotos = computed(() => validarFotos(listaNovasFotos.value, fotosAtuais.value.length))
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
    fotosAtuais.value = data.fotos || []
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível carregar o evento.')
  }
}

async function salvar() {
  erro.value = ''
  if (errosFotos.value.length) {
    erro.value = 'Corrija as fotos antes de salvar: ' + errosFotos.value.join(' ')
    return
  }
  enviando.value = true
  const corpo = {
    titulo: form.titulo,
    descricao: form.descricao || null,
    data: paraApiDataHora(form.data),
    local: form.local,
    limiteVagas: form.limiteVagas,
  }
  let eventoId = route.params.id
  try {
    if (editando.value) {
      await http.put(`/api/eventos/${eventoId}`, corpo)
    } else {
      const { data } = await http.post('/api/eventos', corpo)
      eventoId = data.id
    }
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível salvar o evento.')
    enviando.value = false
    return
  }
  try {
    if (listaNovasFotos.value.length) {
      await enviarFotos(http, eventoId, listaNovasFotos.value)
    }
    router.push({ name: 'admin-eventos' })
  } catch (e) {
    const motivo = mensagemErro(e, 'Não foi possível enviar as fotos.')
    erro.value = `O evento foi salvo, mas as fotos não foram enviadas. ${motivo}`
    if (!editando.value) {
      // Evita criar o evento de novo ao tentar salvar outra vez.
      await router.replace({ name: 'admin-evento-editar', params: { id: eventoId } })
      await carregar()
    }
  } finally {
    enviando.value = false
  }
}

async function removerFoto(fotoId) {
  erro.value = ''
  try {
    await http.delete(`/api/eventos/${route.params.id}/fotos/${fotoId}`)
    fotosAtuais.value = fotosAtuais.value.filter((foto) => foto !== fotoId)
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível remover a foto.')
  }
}

onMounted(carregar)
</script>
