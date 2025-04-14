<script setup>
import { ref, onMounted } from 'vue';
import { login } from '../api/auth';

const emit = defineEmits(['login-success']);

const formData = ref({
  username: '',
  password: '',
  remember: false
});

const loading = ref(false);
const errorMessage = ref('');

// 如果之前记住了用户名，自动填充
onMounted(() => {
  const savedUsername = localStorage.getItem('username');
  if (savedUsername) {
    formData.value.username = savedUsername;
    formData.value.remember = true;
  }
});

const handleLogin = async () => {
  if (!formData.value.username || !formData.value.password) {
    errorMessage.value = '请输入用户名和密码';
    return;
  }

  loading.value = true;
  errorMessage.value = '';

  try {
    const { token, user } = await login(formData.value.username, formData.value.password);
    
    // 保存认证信息
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    
    // 如果选择了"记住我"，保存用户名
    if (formData.value.remember) {
      localStorage.setItem('username', formData.value.username);
    } else {
      localStorage.removeItem('username');
    }
    
    // 触发登录成功事件
    emit('login-success', user);
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请重试';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-form">
    <h2>欢迎登录</h2>
    
    <div class="form-group">
      <label for="username">用户名</label>
      <input
        id="username"
        v-model="formData.username"
        type="text"
        placeholder="请输入用户名"
        @keyup.enter="handleLogin"
      >
    </div>
    
    <div class="form-group">
      <label for="password">密码</label>
      <input
        id="password"
        v-model="formData.password"
        type="password"
        placeholder="请输入密码"
        @keyup.enter="handleLogin"
      >
    </div>
    
    <div class="form-options">
      <label class="remember-me">
        <input
          type="checkbox"
          v-model="formData.remember"
        >
        <span>记住我</span>
      </label>
      <a href="#" class="forgot-password">忘记密码？</a>
    </div>
    
    <div class="error-message" v-if="errorMessage">
      {{ errorMessage }}
    </div>
    
    <button 
      class="login-btn" 
      @click="handleLogin"
      :disabled="loading"
    >
      <span v-if="loading" class="loading-spinner"></span>
      <span v-else>登录</span>
    </button>
    
    <div class="divider">
      <span>或</span>
    </div>
    
    <div class="social-login">
      <button class="social-btn wechat">
        <i class="icon">🔒</i>
        微信登录
      </button>
      <button class="social-btn qq">
        <i class="icon">🔒</i>
        QQ登录
      </button>
    </div>
    
    <div class="register-link">
      还没有账号？<a href="#/register">立即注册</a>
    </div>
    
    <div class="test-accounts">
      <p>测试账号：</p>
      <div class="account-list">
        <div class="account-item">
          <span class="account-type">管理员账号：</span>
          <code>admin / 123456</code>
        </div>
        <div class="account-item">
          <span class="account-type">普通用户：</span>
          <code>user / 123456</code>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-form {
  padding: 30px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-size: 14px;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: all 0.3s;
  background: #f9f9f9;
}

input[type="text"]:focus,
input[type="password"]:focus {
  border-color: #4CAF50;
  outline: none;
  background: white;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.forgot-password {
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}

.error-message {
  color: #ff4757;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
  padding: 8px;
  background: #fff5f5;
  border-radius: 4px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 500;
}

.login-btn:hover {
  background: #45a049;
  transform: translateY(-1px);
}

.login-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #ffffff;
  border-top: 2px solid transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.divider {
  text-align: center;
  margin: 20px 0;
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 30px);
  height: 1px;
  background: #ddd;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: white;
  padding: 0 10px;
  color: #666;
  font-size: 14px;
}

.social-login {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 20px;
}

.social-btn {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
  font-size: 14px;
  color: #333;
}

.social-btn:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.social-btn .icon {
  font-size: 18px;
}

.register-link {
  text-align: center;
  margin: 20px 0;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

.test-accounts {
  margin-top: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
  font-size: 14px;
}

.test-accounts p {
  color: #666;
  margin-bottom: 10px;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.account-type {
  color: #666;
}

code {
  background: #eee;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

@media (max-width: 480px) {
  .login-form {
    padding: 20px;
  }
  
  h2 {
    font-size: 20px;
    margin-bottom: 20px;
  }
  
  .form-options {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .social-login {
    grid-template-columns: 1fr;
  }
}
</style> 