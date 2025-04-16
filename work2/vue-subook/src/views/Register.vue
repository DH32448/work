<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  User, 
  Lock, 
  Message, 
  ChatDotRound, 
  Platform, 
  Connection, 
  Refresh,
  CircleCheck
} from '@element-plus/icons-vue'
import { 
  getRegisterRules, 
  handleRegisterSubmit, 
  checkUsernameAvailability,
  calculatePasswordStrength,
  generateCaptcha
} from '../js/auth/register'
import { useAuth } from '../js/composables/useAuth'

const router = useRouter()
const { handleLoginSuccess } = useAuth()

// 表单数据
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  captcha: '',
  agreement: false
})

// 表单引用
const formRef = ref(null)

// 注册按钮状态
const loading = ref(false)

// 注册成功状态
const registerSuccess = ref(false)

// 用户名检查状态
const usernameCheckStatus = ref({
  checking: false,
  available: null
})

// 验证码相关
const captchaImage = ref(null)
const captchaText = ref(generateCaptcha())
const captchaLoading = ref(false)

// 密码强度相关
const passwordStrength = ref({
  score: 0,
  level: 'empty',
  message: '',
  color: '#DCDFE6'
})

// 表单规则
const rules = getRegisterRules(registerForm.value)

// 处理注册提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用注册处理函数
    handleRegisterSubmit(
      registerForm.value,
      // 成功回调
      (userData) => {
        // 显示注册成功
        registerSuccess.value = true
        
        // 自动登录
        setTimeout(() => {
          handleLoginSuccess(userData)
          router.push('/')
        }, 2000)
      },
      // 失败回调
      (errorMsg) => {
        console.error('注册失败:', errorMsg)
        if (errorMsg === '验证码错误') {
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

// 检查用户名是否可用
const checkUsername = async () => {
  if (!registerForm.value.username || registerForm.value.username.length < 3) return
  
  usernameCheckStatus.value.checking = true
  usernameCheckStatus.value.available = null
  
  try {
    const isAvailable = await checkUsernameAvailability(registerForm.value.username)
    usernameCheckStatus.value.available = isAvailable
  } catch (error) {
    console.error('检查用户名失败', error)
  } finally {
    usernameCheckStatus.value.checking = false
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaLoading.value = true
  
  // 模拟从服务器获取新验证码
  setTimeout(() => {
    captchaText.value = generateCaptcha()
    captchaLoading.value = false
    registerForm.value.captcha = ''
  }, 500)
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 更新密码强度
const updatePasswordStrength = (password) => {
  passwordStrength.value = calculatePasswordStrength(password)
}

// 监听密码变化
const handlePasswordChange = () => {
  updatePasswordStrength(registerForm.value.password)
}

// 计算用户名状态类名
const usernameStatusClass = computed(() => {
  if (usernameCheckStatus.value.checking) return ''
  if (usernameCheckStatus.value.available === null) return ''
  return usernameCheckStatus.value.available ? 'success' : 'error'
})

// 计算用户名状态消息
const usernameStatusMessage = computed(() => {
  if (usernameCheckStatus.value.checking) return '检查中...'
  if (usernameCheckStatus.value.available === null) return ''
  return usernameCheckStatus.value.available ? '用户名可用' : '用户名已被注册'
})

// 密码强度样式
const strengthStyles = computed(() => {
  return {
    color: passwordStrength.value.color
  }
})

// 密码强度条
const getBarClass = (index) => {
  return {
    active: index <= passwordStrength.value.score / 2
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <!-- 页面标题 -->
      <h1 class="page-title">创建您的账号</h1>
      <p class="page-subtitle">加入我们，享受更多精彩内容</p>
      
      <!-- 注册成功提示 -->
      <div v-if="registerSuccess" class="register-success">
        <el-icon class="success-icon"><CircleCheck /></el-icon>
        <h2 class="success-title">注册成功!</h2>
        <p class="success-content">
          您的账号已经创建成功，正在为您自动登录...
        </p>
      </div>
      
      <!-- 注册表单 -->
      <el-form
        v-else
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
        size="large"
        class="register-form"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            @blur="checkUsername"
          />
          <div v-if="usernameStatusMessage" :class="['username-status', usernameStatusClass]">
            <el-icon v-if="usernameCheckStatus.available" style="margin-right: 5px;"><CircleCheck /></el-icon>
            {{ usernameStatusMessage }}
          </div>
        </el-form-item>
        
        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @input="handlePasswordChange"
          />
          <div v-if="registerForm.password" class="password-strength" :style="strengthStyles">
            <div class="strength-bars">
              <div 
                v-for="i in 3" 
                :key="i" 
                class="strength-bar" 
                :class="getBarClass(i)"
              ></div>
            </div>
            <span class="strength-text">{{ passwordStrength.message }}</span>
          </div>
        </el-form-item>
        
        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
          />
        </el-form-item>
        
        <!-- 验证码 -->
        <el-form-item label="验证码" prop="captcha">
          <div class="form-item-captcha">
            <div class="captcha-wrapper">
              <el-input
                v-model="registerForm.captcha"
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
        
        <!-- 用户协议 -->
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            <span class="agreement-text">
              我已阅读并同意
              <el-link type="primary" :underline="false" class="agreement-link">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="false" class="agreement-link">《隐私政策》</el-link>
            </span>
          </el-checkbox>
        </el-form-item>
        
        <!-- 注册按钮 -->
        <div class="register-actions">
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            @click="handleSubmit"
          >
            创建账号
          </el-button>
        </div>
        
        <div class="login-link-container">
          已有账号？
          <span class="login-link" @click="goToLogin">立即登录</span>
        </div>
      </el-form>
      
      <!-- 社交账号注册选项 -->
      <template v-if="!registerSuccess">
        <el-divider class="register-divider">
          <span>其他注册方式</span>
        </el-divider>
        
        <div class="social-signup">
          <el-tooltip content="微信注册" placement="top">
            <el-button circle class="social-icon">
              <el-icon><ChatDotRound /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip content="QQ注册" placement="top">
            <el-button circle class="social-icon">
              <el-icon><Platform /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip content="微博注册" placement="top">
            <el-button circle class="social-icon">
              <el-icon><Connection /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 180px);
  background-color: #f5f7fa;
  padding: 20px;
}

.register-container {
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

.register-form {
  margin-top: 20px;
}

.register-actions {
  margin-top: 24px;
}

.register-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

.username-status {
  font-size: 12px;
  margin-top: 5px;
  display: flex;
  align-items: center;
}

.username-status.success {
  color: #67c23a;
}

.username-status.error {
  color: #f56c6c;
}

.password-strength {
  margin-top: 8px;
  font-size: 12px;
}

.strength-bars {
  display: flex;
  gap: 5px;
  margin-bottom: 4px;
}

.strength-bar {
  height: 4px;
  flex-grow: 1;
  background-color: #dcdfe6;
  border-radius: 2px;
}

.strength-bar.active {
  background-color: currentColor;
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

.register-divider {
  margin: 24px 0;
}

.social-signup {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.social-icon {
  font-size: 20px;
  background-color: #f2f6fc;
}

.login-link-container {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
}

.login-link {
  color: #409eff;
  cursor: pointer;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}

.register-success {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px 0;
}

.success-icon {
  font-size: 60px;
  color: #67c23a;
  margin-bottom: 20px;
}

.success-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 16px;
}

.success-content {
  font-size: 16px;
  color: #606266;
  margin-bottom: 24px;
  line-height: 1.5;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .register-container {
    padding: 20px;
    margin: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
}
</style> 