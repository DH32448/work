<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Lock, 
  ChatDotRound, 
  Platform, 
  Connection,
  Refresh
} from '@element-plus/icons-vue'
import { getLoginRules, handleLoginSubmit } from '../js/auth/login'
import { useAuth } from '../js/composables/useAuth'

const router = useRouter()
const { handleLoginSuccess } = useAuth()

// 表单数据
const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false,
  captcha: ''
})

// 表单引用
const formRef = ref(null)

// 登录按钮状态
const loading = ref(false)

// 验证码相关
const captchaRequired = ref(false)
const captchaText = ref('')
const captchaLoading = ref(false)

// 表单规则
const rules = getLoginRules()

// 登录错误信息
const loginError = ref('')

// 处理登录提交
const handleSubmit = async () => {
  if (!formRef.value) return
  loginError.value = ''
  
  try {
    await formRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用登录处理函数
    handleLoginSubmit(
      loginForm.value,
      // 成功回调
      (userData) => {
        handleLoginSuccess(userData)
        router.push('/')
      },
      // 失败回调
      (errorMsg) => {
        loginError.value = errorMsg
        
        // 如果是密码错误超过次数，显示验证码
        if (errorMsg.includes('密码错误次数过多')) {
          captchaRequired.value = true
          refreshCaptcha()
        }
      },
      // 完成回调
      () => {
        loading.value = false
      }
    )
  } catch (error) {
    console.error('表单验证失败', error)
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaLoading.value = true
  
  // 模拟从服务器获取新验证码
  setTimeout(() => {
    // 生成随机4位验证码
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    let result = ''
    for (let i = 0; i < 4; i++) {
      result += chars[Math.floor(Math.random() * chars.length)]
    }
    
    captchaText.value = result
    captchaLoading.value = false
    loginForm.value.captcha = ''
  }, 500)
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 页面标题 -->
      <h1 class="page-title">欢迎回来</h1>
      <p class="page-subtitle">登录您的账号，继续您的旅程</p>
      
      <!-- 登录表单 -->
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        size="large"
        class="login-form"
      >
        <!-- 错误提示 -->
        <el-alert
          v-if="loginError"
          type="error"
          :title="loginError"
          show-icon
          :closable="false"
          class="login-error"
        />
        
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名或邮箱"
            :prefix-icon="User"
          />
        </el-form-item>
        
        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <!-- 验证码 - 仅在需要时显示 -->
        <el-form-item v-if="captchaRequired" label="验证码" prop="captcha">
          <div class="form-item-captcha">
            <div class="captcha-wrapper">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                maxlength="4"
              >
                <template #suffix>
                  <el-icon 
                    class="refresh-captcha" 
                    @click.stop="refreshCaptcha"
                    :class="{ 'is-loading': captchaLoading }"
                  >
                    <Refresh />
                  </el-icon>
                </template>
              </el-input>
            </div>
            
            <!-- 验证码图片 -->
            <el-image 
              class="captcha-image"
              :src="`data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='120' height='40'><text x='50%' y='50%' font-family='Arial' font-size='24' fill='%23333' text-anchor='middle' dominant-baseline='middle'>${captchaText}</text></svg>`"
              @click="refreshCaptcha"
              :style="{ filter: captchaLoading ? 'blur(2px)' : 'none' }"
              fit="cover"
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <el-icon><Refresh /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
        </el-form-item>
        
        <!-- 记住我 & 忘记密码 -->
        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false" class="forgot-password">忘记密码?</el-link>
        </div>
        
        <!-- 登录按钮 -->
        <div class="login-actions">
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleSubmit"
          >
            登录
          </el-button>
        </div>
        
        <div class="register-link-container">
          还没有账号？
          <span class="register-link" @click="goToRegister">立即注册</span>
        </div>
      </el-form>
      
      <!-- 社交账号登录选项 -->
      <el-divider class="login-divider">
        <span>其他登录方式</span>
      </el-divider>
      
      <div class="social-login">
        <el-tooltip content="微信登录" placement="top">
          <el-button circle class="social-icon">
            <el-icon><ChatDotRound /></el-icon>
          </el-button>
        </el-tooltip>
        
        <el-tooltip content="QQ登录" placement="top">
          <el-button circle class="social-icon">
            <el-icon><Platform /></el-icon>
          </el-button>
        </el-tooltip>
        
        <el-tooltip content="微博登录" placement="top">
          <el-button circle class="social-icon">
            <el-icon><Connection /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 180px);
  background-color: #f5f7fa;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 480px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin: 20px auto;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  text-align: center;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 16px;
  color: #606266;
  text-align: center;
  margin-bottom: 30px;
}

.login-form {
  margin-top: 20px;
}

.login-error {
  margin-bottom: 20px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-actions {
  margin-top: 10px;
}

.login-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

.form-item-captcha {
  display: flex;
  gap: 12px;
}

.captcha-wrapper {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.refresh-captcha {
  cursor: pointer;
  transition: transform 0.3s;
}

.refresh-captcha:hover {
  color: #409eff;
}

.refresh-captcha.is-loading {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-divider {
  margin: 24px 0;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.social-icon {
  font-size: 20px;
  background-color: #f2f6fc;
}

.register-link-container {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
}

.register-link {
  color: #409eff;
  cursor: pointer;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}

.forgot-password {
  font-size: 14px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .login-container {
    padding: 20px;
    margin: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .login-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 