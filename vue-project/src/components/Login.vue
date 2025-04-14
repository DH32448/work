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

const handleClose = () => {
  emit('close');
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
          localStorage.setItem('token', response.data.data);
          localStorage.setItem('user', JSON.stringify({
            username: formData.value.username
          }));
          
          // 如果选择了"记住我"，保存用户名
          if (formData.value.remember) {
            localStorage.setItem('username', formData.value.username);
          } else {
            localStorage.removeItem('username');
          }
          
          // 触发登录成功事件
          emit('login-success', { username: formData.value.username });
          ElMessage.success('登录成功');
          router.push('/');
        } else {
          throw new Error('登录失败：服务器返回数据格式错误');
        }
      } catch (error) {
        errorMessage.value = error.response?.data?.message || '登录失败，请检查用户名和密码';
        ElMessage.error(errorMessage.value);
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="close-button" @click="handleClose">×</div>
      <h2>登录</h2>
      <el-form :model="formData" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="用户名"
            :prefix-icon="User"
            :clearable="false"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="密码"
            :prefix-icon="Lock"
            :clearable="false"
            :show-password="false"
            class="custom-input"
          />
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

/* 隐藏所有后缀图标和清除按钮 */
:deep(.el-input__suffix),
:deep(.el-input__suffix-inner),
:deep(.el-input__clear) {
  display: none !important;
}

/* 确保输入框没有右侧内边距 */
:deep(.el-input__wrapper) {
  padding-right: 11px !important;
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

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    margin: 0 15px;
  }
}
</style> 