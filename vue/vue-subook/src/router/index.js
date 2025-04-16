import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: () => import('@/views/BookDetail.vue'),
    props: true
  },
  {
    path: '/category/:id',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    props: true
  },
  {
    path: '/new-books',
    name: 'NewBooks',
    component: () => import('@/views/NewBooks.vue')
  },
  {
    path: '/hot-books',
    name: 'HotBooks',
    component: () => import('@/views/HotBooks.vue')
  },
  {
    path: '/promotion/:id',
    name: 'Promotion',
    component: () => import('@/views/Promotion.vue'),
    props: true
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 这里应该检查用户是否已登录
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    
    if (!isLoggedIn) {
      // 未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 检查是否需要管理员权限
      if (to.matched.some(record => record.meta.requiresAdmin)) {
        const isAdmin = localStorage.getItem('isAdmin') === 'true'
        
        if (!isAdmin) {
          // 不是管理员，重定向到首页
          next({ path: '/' })
        } else {
          next() // 是管理员，允许访问
        }
      } else {
        next() // 已登录，允许访问
      }
    }
  } else {
    next() // 不需要登录，允许访问
  }
})

export default router 