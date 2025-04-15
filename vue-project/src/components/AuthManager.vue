<script setup>
import { ref, computed } from 'vue';
import Login from './Login.vue';
import Register from './Register.vue';

const emit = defineEmits(['login-success', 'close']);

// 控制显示哪个组件：login 或 register
const currentComponent = ref('login');

// 计算当前显示的组件
const showLogin = computed(() => currentComponent.value === 'login');
const showRegister = computed(() => currentComponent.value === 'register');

// 切换到登录组件
const switchToLogin = () => {
  currentComponent.value = 'login';
};

// 切换到注册组件
const switchToRegister = () => {
  currentComponent.value = 'register';
};

// 处理登录成功
const handleLoginSuccess = (user) => {
  emit('login-success', user);
};

// 处理注册成功
const handleRegisterSuccess = () => {
  // 注册成功后自动切换到登录组件
  switchToLogin();
};

// 关闭模态框
const handleClose = () => {
  emit('close');
};
</script>

<template>
  <div class="auth-manager">
    <Login 
      v-if="showLogin"
      @login-success="handleLoginSuccess"
      @close="handleClose"
      @switch-to-register="switchToRegister"
    />
    
    <Register 
      v-if="showRegister"
      @register-success="handleRegisterSuccess"
      @close="handleClose"
      @switch-to-login="switchToLogin"
    />
  </div>
</template>

<style scoped>
.auth-manager {
  width: 100%;
  height: 100%;
}
</style> 