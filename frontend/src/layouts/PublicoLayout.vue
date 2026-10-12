<template>
  <v-app>
    <v-app-bar class="cabecalho-marca" color="transparent" flat>
      <v-container class="d-flex align-center py-1" fluid>
        <router-link to="/" class="d-flex align-center text-decoration-none">
          <img src="/logo.png" alt="Meninas Digitais UTFPR-CP" class="logo-cabecalho mr-3" />
          <div class="text-white">
            <div class="text-subtitle-1 font-weight-bold lh-1">Meninas Digitais</div>
            <div class="text-caption">UTFPR-CP</div>
          </div>
        </router-link>
        <v-spacer />
        <v-btn variant="text" color="white" to="/" class="d-none d-sm-flex">Eventos</v-btn>
        <v-btn
          v-if="autenticada"
          variant="text"
          color="white"
          to="/admin/eventos"
        >
          Painel
        </v-btn>
        <v-btn v-else variant="outlined" color="white" to="/login">Área da equipe</v-btn>
      </v-container>
    </v-app-bar>

    <v-main class="bg-background">
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { autenticada } from '@/auth/sessao'
</script>
