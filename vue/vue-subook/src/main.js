import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from '@/router'
import './style.css'
import App from './App.vue'

// 创建Pinia实例
const pinia = createPinia()

// 创建Vue应用
const app = createApp(App)

// 使用插件
app.use(ElementPlus)
app.use(pinia)
app.use(router)

// 挂载应用
app.mount('#app')
