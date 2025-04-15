import { createRouter, createWebHistory } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../src/components/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../src/components/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../src/components/Home.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../src/components/About.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/403',
      name: 'forbidden',
      component: () => import('../src/components/Forbidden.vue'),
      meta: { requiresAuth: false }
    }
  ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token || !user) {
      ElMessage.warning('请先登录');
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
    } else {
      // 验证 token 是否过期
      try {
        // 对于JWT令牌，解析payload部分
        if (token.split('.').length === 3) {
          const tokenData = JSON.parse(atob(token.split('.')[1]));
          const currentTime = Date.now() / 1000;
          
          if (tokenData.exp && tokenData.exp < currentTime) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            ElMessage.error('登录已过期，请重新登录');
            next('/login');
            return;
          }
        }
        next();
      } catch (error) {
        console.error('Token验证失败:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        ElMessage.error('登录信息无效，请重新登录');
        next('/login');
      }
    }
  } else {
    if (token && user && to.path === '/login') {
      next('/');
    } else {
      next();
    }
  }
});

export default router; 