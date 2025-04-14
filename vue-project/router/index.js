import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../src/components/Home.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../src/components/About.vue')
    }
  ]
});

export default router; 