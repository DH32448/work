<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { User, Lock, Message, Phone } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import request from '../../ajax/request';
import { register, getVerificationCode } from '../api/auth';

const emit = defineEmits(['register-success', 'close', 'switch-to-login']);
const router = useRouter();
const registerFormRef = ref(null);

const formData = ref({
  username: '',
  email: '',
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  agreement: false
});

// 自定义验证函数：验证用户名
const validateUsername = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'));
  } else if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度应为3-20个字符'));
  } else {
    callback();
  }
};

// 自定义验证函数：验证邮箱
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  
  if (!value) {
    callback(new Error('请输入邮箱'));
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

// 自定义验证函数：验证手机号
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3456789]\d{9}$/;
  
  if (!value) {
    callback(new Error('请输入手机号'));
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号格式'));
  } else {
    callback();
  }
};

// 自定义验证函数：验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'));
  } else if (value !== formData.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 添加表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 6, message: '验证码长度不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ]
};

const loading = ref(false);
const errorMessage = ref('');
const passwordVisible = ref(false);
const confirmPasswordVisible = ref(false);
const registerSuccess = ref(false);
const codeLoading = ref(false);
const codeButtonText = ref('获取验证码');
const codeDisabled = ref(false);

const handleClose = () => {
  emit('close');
};

// 切换到登录页面
const switchToLogin = () => {
  emit('switch-to-login');
};

// 切换密码显示/隐藏状态
const togglePasswordVisibility = (field) => {
  if (field === 'password') {
    passwordVisible.value = !passwordVisible.value;
  } else if (field === 'confirmPassword') {
    confirmPasswordVisible.value = !confirmPasswordVisible.value;
  }
};

// 处理按 Enter 键提交
const handleKeyDown = (e) => {
  if (e.key === 'Enter') {
    handleRegister();
  }
};

// 获取验证码
const handleGetCode = async () => {
  // 先验证邮箱
  try {
    await registerFormRef.value.validateField('email');
    codeLoading.value = true;
    codeDisabled.value = true;
    
    console.log('准备获取验证码，邮箱:', formData.value.email);
    
    try {
      const response = await getVerificationCode(formData.value.email);
      console.log('验证码完整响应:', response);
      
      if (response && response.success) {
        ElMessage.success(response.message || '验证码已发送到您的邮箱');
        
        // 倒计时
        let countdown = 60;
        const timer = setInterval(() => {
          countdown--;
          codeButtonText.value = `${countdown}秒后重新获取`;
          if (countdown <= 0) {
            clearInterval(timer);
            codeButtonText.value = '获取验证码';
            codeDisabled.value = false;
          }
        }, 1000);
      } else {
        throw new Error(response.message || '获取验证码失败');
      }
    } catch (error) {
      console.error('获取验证码详细错误:', error);
      console.error('错误响应数据:', error.response?.data);
      console.error('错误状态码:', error.response?.status);
      ElMessage.error(error.response?.data?.message || error.message || '获取验证码失败');
      codeDisabled.value = false;
      codeButtonText.value = '获取验证码';
    } finally {
      codeLoading.value = false;
    }
  } catch (error) {
    ElMessage.warning('请先输入正确的邮箱地址');
  }
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      errorMessage.value = '';
      
      console.log('注册表单数据:', {
        username: formData.value.username,
        email: formData.value.email,
        phone: formData.value.phone,
        code: formData.value.code,
        password: formData.value.password
      });

      try {
        const response = await register(
          formData.value.username,
          formData.value.email,
          formData.value.phone,
          formData.value.code,
          formData.value.password
        );
        
        console.log('注册响应详情:', response);
        
        if (response && response.success) {
          // 显示注册成功的过渡动画
          registerSuccess.value = true;
          
          // 延迟处理，以便显示动画
          setTimeout(() => {
            emit('register-success');
            ElMessage.success(response.message || '注册成功，请登录');
            // 清空表单
            formData.value = {
              username: '',
              email: '',
              phone: '',
              code: '',
              password: '',
              confirmPassword: '',
              agreement: false
            };
            // 跳转到登录页
            switchToLogin();
          }, 800);
        } else {
          throw new Error(response.message || '注册失败，请检查您的输入');
        }
      } catch (error) {
        console.error('注册详细错误:', error);
        console.error('错误响应数据:', error.response?.data);
        console.error('错误状态码:', error.response?.status);
        registerSuccess.value = false;
        errorMessage.value = error.response?.data?.message || error.message || '注册失败，请稍后再试';
        ElMessage.error(errorMessage.value);
      } finally {
        if (!registerSuccess.value) {
          loading.value = false;
        }
      }
    }
  });
};
</script>

<template>
  <div class="register-container">
    <div 
      class="register-box" 
      :class="{ 'register-success': registerSuccess }"
      @keydown="handleKeyDown"
    >
      <div class="close-button" @click="handleClose">×</div>
      <h2>注册</h2>
      <el-form :model="formData" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input 
            v-model="formData.email" 
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input 
            v-model="formData.phone" 
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            clearable
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="code">
          <div class="code-input-group">
            <el-input 
              v-model="formData.code" 
              placeholder="请输入验证码"
              class="custom-input code-input"
            />
            <el-button 
              type="primary" 
              @click="handleGetCode" 
              :loading="codeLoading" 
              :disabled="codeDisabled"
              class="code-button"
            >
              {{ codeButtonText }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="formData.password" 
            :type="passwordVisible ? 'text' : 'password'" 
            placeholder="请输入密码"
            :prefix-icon="Lock"
            class="custom-input"
          >
            <template #suffix>
              <span 
                class="password-toggle" 
                @click="togglePasswordVisibility('password')"
              >
                {{ passwordVisible ? '隐藏' : '显示' }}
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="formData.confirmPassword" 
            :type="confirmPasswordVisible ? 'text' : 'password'" 
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            class="custom-input"
          >
            <template #suffix>
              <span 
                class="password-toggle" 
                @click="togglePasswordVisibility('confirmPassword')"
              >
                {{ confirmPasswordVisible ? '隐藏' : '显示' }}
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="agreement">
          <el-checkbox v-model="formData.agreement">我已阅读并同意<a href="#" class="agreement-link">用户协议</a></el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading" 
            class="register-button"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-options">
        <span>已有账号？</span>
        <a href="#" class="login-link" @click.prevent="switchToLogin">立即登录</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.register-box {
  background: white;
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.5s ease;
}

/* 注册成功动画 */
.register-success {
  transform: scale(0.9) translateY(-20px);
  opacity: 0;
}

.close-button {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 18px;
  cursor: pointer;
  color: #999;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  color: #333;
}

h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #303133;
}

.custom-input {
  width: 100%;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.code-button {
  width: 120px;
  min-width: 120px;
}

.password-toggle {
  font-size: 12px;
  color: #409EFF;
  cursor: pointer;
  user-select: none;
}

.register-form {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
}

.agreement-link {
  color: #409EFF;
  text-decoration: none;
}

.register-options {
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.login-link {
  color: #409EFF;
  text-decoration: none;
  cursor: pointer;
}

.login-link:hover {
  text-decoration: underline;
}
</style> 