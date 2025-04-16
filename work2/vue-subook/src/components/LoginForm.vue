<script setup>
import { ref, reactive } from 'vue'
import { 
  Lock, User, ChatDotRound, Platform, Connection
} from '@element-plus/icons-vue'
import { handleLoginSubmit, getLoginRules } from '../js/auth/login'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'login-success', 'switch-to-register'])

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单规则
const rules = reactive(getLoginRules())

// 加载状态
const loading = ref(false)
// 表单引用
const loginFormRef = ref(null)

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 处理登录提交
    handleLoginSubmit(
      loginForm,
      // 成功回调
      (userData) => {
        emit('login-success', userData)
        emit('update:modelValue', false)
        loginFormRef.value.resetFields()
      },
      // 失败回调
      () => {},
      // 完成回调
      () => {
        loading.value = false
      }
    )
  } catch (error) {
    console.error('表单验证失败', error)
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:modelValue', false)
  // 重置表单
  loginFormRef.value?.resetFields()
}

// 切换到注册对话框
const switchToRegister = () => {
  emit('switch-to-register')
}
</script>

<template>
  <el-dialog
    title="用户登录"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    width="400px"
    @close="handleClose"
    destroy-on-close
    custom-class="login-dialog"
  >
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="rules"
      label-position="top"
      size="large"
      class="login-form"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
        />
      </el-form-item>
      
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          :prefix-icon="Lock"
          show-password
        />
      </el-form-item>
      
      <el-form-item>
        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码?</el-link>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          class="login-button"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form-item>
      
      <el-divider>
        <span class="divider-text">其他登录方式</span>
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
      
      <div class="register-link">
        <span>还没有账号?</span>
        <el-link type="primary" :underline="false" @click="switchToRegister">立即注册</el-link>
      </div>
      
      <div class="test-account">
        <el-alert
          title="测试账号: user / 123456"
          type="info"
          :closable="false"
          effect="light"
        />
      </div>
    </el-form>
  </el-dialog>
</template> 