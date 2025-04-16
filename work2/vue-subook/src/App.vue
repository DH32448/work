<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { 
  User, List, Star, Setting, SwitchButton, ArrowDown
} from '@element-plus/icons-vue'
import LoginForm from './components/LoginForm.vue'
import RegisterForm from './components/RegisterForm.vue'
import { useAuth } from './js/composables/useAuth'

const router = useRouter()
const activeIndex = '/'

// 使用认证状态组合式API
const { 
  isLoggedIn, 
  userInfo, 
  loginDialogVisible, 
  showLoginDialog, 
  handleLoginSuccess, 
  logout 
} = useAuth()

// 注册对话框状态
const registerDialogVisible = ref(false)

const handleSelect = (key) => {
  router.push(key)
}

// 用户菜单数据
const userMenuItems = [
  { key: 'profile', label: '个人中心', icon: User },
  { key: 'orders', label: '我的订单', icon: List },
  { key: 'favorites', label: '我的收藏', icon: Star },
  { key: 'settings', label: '设置', icon: Setting },
  { key: 'logout', label: '退出登录', icon: SwitchButton }
]

// 处理用户菜单项点击
const handleUserMenuCommand = (command) => {
  if (command === 'logout') {
    // 退出登录逻辑
    logout()
  } else if (command === 'profile') {
    // 导航到个人中心
    router.push('/profile')
  } else {
    // 导航到其他页面
    console.log(`导航到: ${command}`)
  }
}

// 显示注册对话框
const showRegisterDialog = () => {
  registerDialogVisible.value = true
}

// 从登录切换到注册
const switchToRegister = () => {
  loginDialogVisible.value = false
  registerDialogVisible.value = true
}

// 从注册切换到登录
const switchToLogin = () => {
  registerDialogVisible.value = false
  loginDialogVisible.value = true
}

// 处理注册成功
const handleRegisterSuccess = (userData) => {
  // 在实际应用中可能需要进行一些处理
  console.log('注册成功:', userData)
}
</script>

<template>
  <el-container class="app-container">
    <el-header height="60px">
      <div class="header-content">
        <div class="logo">
          <img src="./assets/vue.svg" alt="Vue logo" />
          <h1>Su Book</h1>
        </div>
        
        <div class="navigation-container">
          <el-menu
            :default-active="$route.path"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
            background-color="#ffffff"
            text-color="#333"
            active-text-color="#409EFF"
            :ellipsis="false"
            router
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/bookstore">书城</el-menu-item>
            <el-menu-item index="/about">关于</el-menu-item>
          </el-menu>
          
          <!-- 登录或用户头像 -->
          <div v-if="isLoggedIn" class="user-avatar-container">
            <el-dropdown trigger="hover" @command="handleUserMenuCommand">
              <div class="avatar-wrapper">
                <el-avatar 
                  size="medium" 
                  :src="userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
                />
                <span class="username-text">{{ userInfo?.username }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item 
                    v-for="item in userMenuItems" 
                    :key="item.key" 
                    :command="item.key"
                  >
                    <el-icon><component :is="item.icon" /></el-icon>
                    <span style="margin-left: 8px;">{{ item.label }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div v-else class="login-container">
            <el-button type="primary" @click="showLoginDialog">登录</el-button>
            <el-button @click="showRegisterDialog">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>
    
    <el-main>
      <div class="main-content">
        <!-- 路由视图 -->
        <router-view />
      </div>
    </el-main>
    
    <el-footer height="60px">
      <div class="footer-content">
        <p>© 2023 Su Book - Vue 3 + Element Plus + Pinia + Axios 示例项目</p>
      </div>
    </el-footer>
    
    <!-- 登录表单组件 -->
    <LoginForm 
      v-model="loginDialogVisible"
      @login-success="handleLoginSuccess"
      @switch-to-register="switchToRegister"
    />
    
    <!-- 注册表单组件 -->
    <RegisterForm
      v-model="registerDialogVisible"
      @register-success="handleRegisterSuccess"
      @switch-to-login="switchToLogin"
    />
  </el-container>
</template>
