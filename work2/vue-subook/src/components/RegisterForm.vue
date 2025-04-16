<script setup>
import { ref, reactive, computed, watch } from 'vue'
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

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'register-success', 'switch-to-login'])

// 表单数据
const registerForm = reactive({
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
const usernameCheckStatus = reactive({
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
const rules = getRegisterRules(registerForm)

// 更新密码强度
watch(() => registerForm.password, (newPassword) => {
  passwordStrength.value = calculatePasswordStrength(newPassword)
})

// 监听验证码输入，自动大写
watch(() => registerForm.captcha, (value) => {
  if (value) {
    registerForm.captcha = value.toUpperCase()
  }
})

// 处理注册提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用注册处理函数
    handleRegisterSubmit(
      registerForm,
      // 成功回调
      (userData) => {
        // 显示注册成功
        registerSuccess.value = true
        
        // 发出注册成功事件
        emit('register-success', userData)
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

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  usernameCheckStatus.checking = false
  usernameCheckStatus.available = null
  registerSuccess.value = false
  passwordStrength.value = {
    score: 0,
    level: 'empty',
    message: '',
    color: '#DCDFE6'
  }
}

// 处理关闭对话框
const handleClose = () => {
  emit('update:modelValue', false)
  resetForm()
}

// 切换到登录对话框
const switchToLogin = () => {
  emit('switch-to-login')
  handleClose()
}

// 检查用户名是否可用
const checkUsername = async () => {
  if (!registerForm.username || registerForm.username.length < 3) return
  
  usernameCheckStatus.checking = true
  usernameCheckStatus.available = null
  
  try {
    const isAvailable = await checkUsernameAvailability(registerForm.username)
    usernameCheckStatus.available = isAvailable
  } catch (error) {
    console.error('检查用户名失败', error)
  } finally {
    usernameCheckStatus.checking = false
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaLoading.value = true
  
  // 模拟从服务器获取新验证码
  setTimeout(() => {
    captchaText.value = generateCaptcha()
    captchaLoading.value = false
    registerForm.captcha = ''
  }, 500)
}

// 生成指示器类名
const usernameStatusClass = computed(() => {
  if (usernameCheckStatus.checking) return ''
  if (usernameCheckStatus.available === null) return ''
  return usernameCheckStatus.available ? 'success' : 'error'
})

// 生成指示器消息
const usernameStatusMessage = computed(() => {
  if (usernameCheckStatus.checking) return '检查中...'
  if (usernameCheckStatus.available === null) return ''
  return usernameCheckStatus.available ? '用户名可用' : '用户名已被注册'
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
  <el-dialog
    title="用户注册"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    width="400px"
    @close="handleClose"
    destroy-on-close
    custom-class="register-dialog"
  >
    <!-- 注册成功提示 -->
    <div v-if="registerSuccess" class="register-success">
      <el-icon class="success-icon"><CircleCheck /></el-icon>
      <h2 class="success-title">注册成功!</h2>
      <p class="success-content">
        您的账号已经创建成功，现在可以登录使用所有功能。
      </p>
      <div class="success-actions">
        <el-button @click="switchToLogin" type="primary">立即登录</el-button>
      </div>
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
        />
        <div v-if="registerForm.password" class="password-strength" :style="strengthStyles">
          <div class="strength-bars">
            <div 
              v-for="i in 3" 
              :key="i" 
              class="strength-bar" 
              :class="getBarClass(i)"
              :style="{ color: passwordStrength.color }"
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
          注册
        </el-button>
      </div>
    </el-form>
    
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
      
      <div class="login-link-container">
        已有账号？
        <span class="login-link" @click="switchToLogin">立即登录</span>
      </div>
    </template>
  </el-dialog>
</template> 