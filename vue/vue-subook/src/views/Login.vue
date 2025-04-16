<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box">
        <div class="login-left">
          <div class="welcome-content">
            <h1 class="welcome-title">欢迎使用</h1>
            <h2 class="brand-name">Su Book</h2>
            <p class="welcome-desc">您的专属阅读平台，发现无限可能</p>
            <div class="decoration-img">
              <img src="https://img.alicdn.com/imgextra/i3/O1CN01qNXCuO1O086R2Dcj0_!!6000000001639-2-tps-846-786.png" alt="decoration">
            </div>
          </div>
        </div>
        
        <div class="login-right">
          <div class="login-form-container">
            <div class="form-header">
              <h2>用户登录</h2>
              <p class="sub-title">登录后享受更多精彩内容</p>
            </div>
            
            <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
              <el-form-item prop="username">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="请输入用户名" 
                  :prefix-icon="User"
                  size="large"
                  class="custom-input"
                ></el-input>
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  show-password 
                  placeholder="请输入密码" 
                  :prefix-icon="Lock"
                  size="large"
                  class="custom-input"
                ></el-input>
              </el-form-item>
              
              <div class="form-options">
                <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
                <el-link type="primary" class="forget-link">忘记密码?</el-link>
              </div>
              
              <el-button 
                type="primary" 
                :loading="loading" 
                @click="handleLogin" 
                class="login-button"
                size="large"
                round
              >登录</el-button>
              
              <div class="register-link">
                还没有账号? <el-link type="primary" @click="toRegister">立即注册</el-link>
              </div>
            </el-form>
            
            <div class="other-login">
              <div class="divider">
                <span>其他登录方式</span>
              </div>
              
              <div class="social-login">
                <div class="social-icon">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="social-icon">
                  <el-icon><Phone /></el-icon>
                </div>
                <div class="social-icon">
                  <el-icon><Service /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Phone, Service } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 模拟登录请求
      setTimeout(() => {
        ElMessage.success('登录成功')
        // 存储登录状态
        localStorage.setItem('isLoggedIn', 'true')
        loading.value = false
        router.push('/')
      }, 1500)
    } else {
      return false
    }
  })
}

const toRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-container {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.login-box {
  display: flex;
  border-radius: 12px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.1);
  height: 600px;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1a6fc9 0%, #2a8cf2 100%);
  color: white;
  padding: 40px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-content {
  position: relative;
  z-index: 2;
}

.welcome-title {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 600;
}

.brand-name {
  font-size: 42px;
  margin-bottom: 20px;
  font-weight: 700;
  background: linear-gradient(to right, #ffffff, #94c6ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-desc {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
  max-width: 280px;
  line-height: 1.6;
}

.decoration-img {
  position: absolute;
  bottom: -20px;
  right: -40px;
  width: 70%;
  opacity: 0.2;
}

.decoration-img img {
  width: 100%;
}

.login-right {
  flex: 1;
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form-container {
  width: 100%;
  max-width: 380px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.sub-title {
  color: #909399;
  font-size: 14px;
}

.custom-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.1) inset;
  padding: 0 15px;
  border-radius: 8px;
  height: 50px;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  margin-bottom: 20px;
  background: linear-gradient(to right, #1a6fc9, #2a8cf2);
  border: none;
}

.register-link {
  text-align: center;
  margin-bottom: 30px;
  color: #606266;
}

.other-login {
  margin-top: 30px;
}

.divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.divider::before,
.divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background-color: #e4e7ed;
}

.divider span {
  padding: 0 15px;
  font-size: 14px;
  color: #909399;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 15px;
}

.social-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #f2f6fc;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.social-icon:hover {
  background-color: #ecf5ff;
  transform: translateY(-2px);
}

.social-icon .el-icon {
  font-size: 20px;
  color: #1a6fc9;
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    height: auto;
  }
  
  .login-left {
    padding: 30px;
    min-height: 200px;
  }
  
  .login-right {
    padding: 30px;
  }
  
  .decoration-img {
    display: none;
  }
}
</style> 