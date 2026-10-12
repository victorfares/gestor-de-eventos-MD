<template>
  <v-app>
    <v-app-bar class="cabecalho-marca" color="transparent" flat>
      <v-container class="d-flex align-center py-1" fluid>
        <router-link to="/admin/eventos" class="d-flex align-center text-decoration-none">
          <img src="/logo.png" alt="Meninas Digitais UTFPR-CP" class="logo-cabecalho mr-3" />
          <div class="text-white">
            <div class="text-subtitle-1 font-weight-bold lh-1">Painel da equipe</div>
            <div class="text-caption">{{ usuarioLogado?.nome }}</div>
          </div>
        </router-link>
        <v-spacer />
        <v-btn variant="text" color="white" to="/admin/eventos">Eventos</v-btn>
        <v-btn variant="text" color="white" to="/admin/membros">Membros</v-btn>
        <v-btn variant="text" color="white" to="/">Vitrine</v-btn>
        <v-btn variant="outlined" color="white" @click="sair">Sair</v-btn>
      </v-container>
    </v-app-bar>

    <v-main class="bg-background">
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { encerrarSessao, usuarioLogado } from '@/auth/sessao'

const router = useRouter()

function sair() {
  encerrarSessao()
  router.push({ name: 'login' })
}
</script>
