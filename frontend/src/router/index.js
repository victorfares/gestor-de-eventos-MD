import { createRouter, createWebHistory } from 'vue-router'
import { autenticada } from '@/auth/sessao'
import PublicoLayout from '@/layouts/PublicoLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior() {
    return { top: 0 }
  },
  routes: [
    {
      path: '/',
      component: PublicoLayout,
      children: [
        {
          path: '',
          name: 'vitrine',
          component: () => import('@/views/publico/VitrineView.vue'),
        },
        {
          path: 'eventos/:id',
          name: 'evento-detalhe',
          component: () => import('@/views/publico/EventoDetalheView.vue'),
        },
        {
          path: 'login',
          name: 'login',
          component: () => import('@/views/admin/LoginView.vue'),
        },
      ],
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: { name: 'admin-eventos' } },
        {
          path: 'eventos',
          name: 'admin-eventos',
          component: () => import('@/views/admin/EventosAdminView.vue'),
        },
        {
          path: 'eventos/novo',
          name: 'admin-evento-novo',
          component: () => import('@/views/admin/EventoFormView.vue'),
        },
        {
          path: 'eventos/:id/editar',
          name: 'admin-evento-editar',
          component: () => import('@/views/admin/EventoFormView.vue'),
        },
        {
          path: 'eventos/:id/inscritas',
          name: 'admin-inscritas',
          component: () => import('@/views/admin/InscritasView.vue'),
        },
        {
          path: 'membros',
          name: 'admin-membros',
          component: () => import('@/views/admin/MembrosView.vue'),
        },
      ],
    },
    { path: '/:caminho(.*)*', redirect: { name: 'vitrine' } },
  ],
})

router.beforeEach((para) => {
  if (para.meta.requiresAuth && !autenticada.value) {
    return { name: 'login', query: { redirect: para.fullPath } }
  }
  if (para.name === 'login' && autenticada.value) {
    return { name: 'admin-eventos' }
  }
  return true
})

export default router
