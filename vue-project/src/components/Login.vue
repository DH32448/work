<script setup>
import { ref, onMounted, reactive } from 'vue';
import { login } from '../api/auth';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import request from '../../ajax/request';

const emit = defineEmits(['login-success', 'close']);
const router = useRouter();
const loginFormRef = ref(null);

const formData = ref({
  username: '',
  password: '',
  remember: false
});

// 自定义验证函数：验证用户名是电话号码或邮箱
const validateUsername = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  const phoneRegex = /^1[3456789]\d{9}$/;
  
  if (!value) {
    callback(new Error('请输入账号'));
  } else if (emailRegex.test(value) || phoneRegex.test(value)) {
    callback();
  } else {
    callback(new Error('请输入正确的手机号或邮箱'));
  }
};

// 添加表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ]
};

const loading = ref(false);
const errorMessage = ref('');
const passwordVisible = ref(false); // 控制密码显示/隐藏
const loginSuccess = ref(false); // 控制登录成功的过渡动画

// 如果之前记住了用户名和密码，自动填充
onMounted(() => {
  const savedUsername = localStorage.getItem('username');
  const savedPassword = localStorage.getItem('password');
  
  if (savedUsername) {
    formData.value.username = savedUsername;
    formData.value.remember = true;
  }
  
  if (savedPassword && formData.value.remember) {
    try {
      // 简单的解密方法（实际项目中应使用更安全的加密方式）
      formData.value.password = atob(savedPassword);
    } catch (e) {
      console.error('密码解密失败');
    }
  }
});

const handleClose = () => {
  emit('close');
};

// 切换密码显示/隐藏状态
const togglePasswordVisibility = () => {
  passwordVisible.value = !passwordVisible.value;
};

// 处理按 Enter 键提交
const handleKeyDown = (e) => {
  if (e.key === 'Enter') {
    handleLogin();
  }
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      errorMessage.value = '';

      try {
        const params = new URLSearchParams();
        params.append('username', formData.value.username);
        params.append('password', formData.value.password);

        const response = await request({
          url: '/api/auth/login',
          method: 'post',
          data: params,
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        });
        
        // 保存认证信息
        if (response.data && response.data.data) {
          // 显示登录成功的过渡动画
          loginSuccess.value = true;
          
          // 延迟保存数据和跳转，以便显示动画
          setTimeout(() => {
            const token = response.data.data;
            
            // 确保token格式正确，可以被后续认证流程正确解析
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify({
              username: formData.value.username
            }));
            
            // 如果选择了"记住我"，保存用户名和密码
            if (formData.value.remember) {
              localStorage.setItem('username', formData.value.username);
              // 简单的加密方法（实际项目中应使用更安全的加密方式）
              localStorage.setItem('password', btoa(formData.value.password));
            } else {
              localStorage.removeItem('username');
              localStorage.removeItem('password');
            }
            
            // 触发登录成功事件
            emit('login-success', { username: formData.value.username });
            ElMessage.success('登录成功');
            router.push('/');
          }, 800); // 动画持续时间
        } else {
          throw new Error('登录失败：服务器返回数据格式错误');
        }
      } catch (error) {
        loginSuccess.value = false;
        errorMessage.value = error.response?.data?.message || '登录失败，请检查账号和密码';
        ElMessage.error(errorMessage.value);
      } finally {
        if (!loginSuccess.value) {
          loading.value = false;
        }
      }
    }
  });
};
</script>

<template>
  <div class="login-container">
    <div 
      class="login-box" 
      :class="{ 'login-success': loginSuccess }"
      @keydown="handleKeyDown"
    >
      <div class="close-button" @click="handleClose">×</div>
      <h2>登录</h2>
      <el-form :model="formData" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入手机号或邮箱"
            :prefix-icon="User"
            clearable
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="formData.password" 
            :type="passwordVisible ? 'text' : 'password'" 
            placeholder="密码"
            :prefix-icon="Lock"
            class="custom-input"
          >
            <template #suffix>
              <span 
                class="password-toggle" 
                @click="togglePasswordVisibility"
              >
                {{ passwordVisible ? '隐藏' : '显示' }}
              </span>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <div class="remember-line">
            <el-checkbox v-model="formData.remember">记住密码</el-checkbox>
            <a href="#" class="forgot-password">忘记密码?</a>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading" 
            class="login-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-options">
        <span>还没有账号？</span>
        <a href="#" class="register-link">立即注册</a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
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

.login-box {
  background: white;
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.5s ease;
}

/* 登录成功动画 */
.login-success {
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
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-button:hover {
  background-color: rgba(0, 0, 0, 0.1);
  color: #666;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
}

.login-form {
  margin-top: 20px;
}

:deep(.custom-input .el-input__wrapper) {
  background-color: #f5f7fa;
  border: none;
  height: 40px;
  padding: 0 15px;
}

:deep(.custom-input .el-input__wrapper:hover) {
  background-color: #ebeef5;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  background-color: white;
  box-shadow: 0 0 0 1px #409EFF;
}

.password-toggle {
  font-size: 12px;
  color: #409EFF;
  cursor: pointer;
  user-select: none;
}

.remember-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-password {
  font-size: 14px;
  color: #909399;
  text-decoration: none;
}

.forgot-password:hover {
  color: #409EFF;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  background: #409EFF;
  border: none;
  margin-top: 10px;
}

.login-button:hover {
  background: #66b1ff;
}

.login-options {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.register-link {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    margin: 0 15px;
  }
}
</style> 