<template>
  <v-container class="py-8">
    <h1 class="text-h5 font-weight-bold text-primary mb-4">Membros da equipe</h1>
    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
    <v-alert v-if="aviso" type="success" variant="tonal" class="mb-4">{{ aviso }}</v-alert>

    <v-card class="mb-6">
      <v-card-title class="d-flex align-center">
        Solicitações de cadastro
        <v-chip v-if="solicitacoes.length" color="secondary" size="small" class="ml-2">
          {{ solicitacoes.length }} pendente{{ solicitacoes.length > 1 ? 's' : '' }}
        </v-chip>
      </v-card-title>
      <v-card-text>
        <p v-if="!carregando && !solicitacoes.length" class="text-medium-emphasis mb-0">
          Nenhuma solicitação aguardando aprovação.
        </p>
        <v-data-table
          v-else
          :headers="colunasSolicitacoes"
          :items="solicitacoes"
          :loading="carregando"
          item-value="id"
          hide-default-footer
          class="bg-surface"
        >
          <template #item.criadoEm="{ item }">
            {{ item.criadoEm ? formatarDataHora(item.criadoEm) : '-' }}
          </template>
          <template #item.acoes="{ item }">
            <div class="d-flex ga-2 justify-end">
              <v-btn
                size="small"
                color="primary"
                :loading="processando === item.id"
                @click="aprovar(item)"
              >
                Aprovar
              </v-btn>
              <v-btn
                size="small"
                variant="outlined"
                color="error"
                :disabled="processando === item.id"
                @click="pedirRecusa(item)"
              >
                Recusar
              </v-btn>
            </div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

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

    <v-dialog v-model="dialogoRecusa" max-width="420">
      <v-card>
        <v-card-title>Recusar solicitação?</v-card-title>
        <v-card-text>
          A solicitação de <strong>{{ recusando?.nome }}</strong> ({{ recusando?.email }}) será apagada.
          A pessoa poderá enviar um novo pedido depois.
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="dialogoRecusa = false">Cancelar</v-btn>
          <v-btn color="error" :loading="processando === recusando?.id" @click="recusar">Recusar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import http, { mensagemErro } from '@/api/http'
import { formatarDataHora } from '@/formatacao'

const membros = ref([])
const solicitacoes = ref([])
const carregando = ref(false)
const enviando = ref(false)
const processando = ref(null)
const dialogoRecusa = ref(false)
const recusando = ref(null)
const erro = ref('')
const aviso = ref('')
const form = reactive({ nome: '', email: '', senha: '' })
const colunas = [
  { title: 'Nome', key: 'nome' },
  { title: 'E-mail', key: 'email' },
]
const colunasSolicitacoes = [
  { title: 'Nome', key: 'nome' },
  { title: 'E-mail', key: 'email' },
  { title: 'Pedido em', key: 'criadoEm' },
  { title: '', key: 'acoes', sortable: false, align: 'end' },
]

async function carregar() {
  carregando.value = true
  try {
    const [respostaMembros, respostaSolicitacoes] = await Promise.all([
      http.get('/api/usuarios'),
      http.get('/api/usuarios/solicitacoes'),
    ])
    membros.value = respostaMembros.data
    solicitacoes.value = respostaSolicitacoes.data
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível listar os membros.')
  } finally {
    carregando.value = false
  }
}

function limparMensagens() {
  erro.value = ''
  aviso.value = ''
}

async function aprovar(solicitacao) {
  limparMensagens()
  processando.value = solicitacao.id
  try {
    await http.post(`/api/usuarios/solicitacoes/${solicitacao.id}/aprovar`)
    aviso.value = `${solicitacao.nome} agora é membro da equipe e já pode entrar no painel.`
    await carregar()
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível aprovar o cadastro.')
  } finally {
    processando.value = null
  }
}

function pedirRecusa(solicitacao) {
  recusando.value = solicitacao
  dialogoRecusa.value = true
}

async function recusar() {
  limparMensagens()
  const solicitacao = recusando.value
  processando.value = solicitacao.id
  try {
    await http.delete(`/api/usuarios/solicitacoes/${solicitacao.id}`)
    aviso.value = `A solicitação de ${solicitacao.nome} foi recusada.`
    dialogoRecusa.value = false
    await carregar()
  } catch (e) {
    erro.value = mensagemErro(e, 'Não foi possível recusar o cadastro.')
  } finally {
    processando.value = null
  }
}

async function criar() {
  limparMensagens()
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
