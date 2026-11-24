<template>
  <v-app>
    <v-app-bar class="cabecalho-marca cabecalho-publico" color="transparent" flat>
      <router-link to="/" class="marca">
        <img src="/logo.png" alt="" class="logo-cabecalho" />
        <div>
          <div class="marca-nome">Meninas Digitais</div>
          <div class="marca-campus">UTFPR-CP</div>
        </div>
      </router-link>
      <router-link class="link-equipe" :to="autenticada ? '/admin/eventos' : '/login'">
        {{ autenticada ? 'Painel' : 'Equipe' }}
      </router-link>
    </v-app-bar>

    <v-main class="bg-background">
      <router-view v-slot="{ Component }">
        <transition name="pagina" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </v-main>
  </v-app>
</template>

<script setup>
import { autenticada } from '@/auth/sessao'
</script>
