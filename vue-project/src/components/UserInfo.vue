<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserInfo } from '../api/auth';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'update']);

// 用户基本信息
const user = ref(null);
// 用户详细信息
const userInfo = ref(null);
// 加载状态
const loading = ref(false);
// 错误信息
const errorMessage = ref('');

// 计算属性：格式化注册时间
const formattedRegisterTime = computed(() => {
  if (!userInfo.value || !userInfo.value.registerTime) return '未知';
  return userInfo.value.registerTime;
});

// 获取本地存储的用户信息
const getUserFromLocalStorage = () => {
  const userString = localStorage.getItem('user');
  if (userString) {
    try {
      user.value = JSON.parse(userString);
    } catch (e) {
      console.error('解析用户信息失败:', e);
      user.value = null;
    }
  }
  
  const userInfoString = localStorage.getItem('userInfo');
  if (userInfoString) {
    try {
      userInfo.value = JSON.parse(userInfoString);
    } catch (e) {
      console.error('解析用户详细信息失败:', e);
      userInfo.value = null;
    }
  }
};

// 从服务器获取用户信息
const fetchUserInfo = async () => {
  if (!user.value) {
    errorMessage.value = '未找到用户信息，请先登录';
    return;
  }
  
  loading.value = true;
  errorMessage.value = '';
  
  try {
    // 使用用户名或邮箱获取用户信息
    const identifier = user.value.email || user.value.username;
    const response = await getUserInfo(identifier);
    
    if (response && response.success) {
      userInfo.value = response.data;
      ElMessage.success('获取用户信息成功');
    } else {
      errorMessage.value = response.message || '获取用户信息失败';
      ElMessage.error(errorMessage.value);
    }
  } catch (error) {
    console.error('获取用户信息出错:', error);
    errorMessage.value = error.message || '获取用户信息时发生错误';
    ElMessage.error(errorMessage.value);
  } finally {
    loading.value = false;
  }
};

// 关闭对话框
const handleClose = () => {
  emit('close');
};

// 刷新用户信息
const refreshUserInfo = () => {
  fetchUserInfo();
};

// 组件挂载时获取用户信息
onMounted(() => {
  getUserFromLocalStorage();
  if (props.visible && user.value && !userInfo.value) {
    fetchUserInfo();
  }
});
</script>

<template>
  <div v-if="visible" class="user-info-container">
    <div class="user-info-box">
      <div class="close-button" @click="handleClose">×</div>
      <h2>用户信息</h2>
      
      <div v-if="loading" class="loading-status">
        <el-icon class="is-loading"><loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="errorMessage" class="error-message">
        <el-alert
          :title="errorMessage"
          type="error"
          show-icon
          :closable="false"
        />
        <el-button type="primary" @click="refreshUserInfo" class="refresh-button">
          重试
        </el-button>
      </div>
      
      <div v-else-if="userInfo" class="user-info-content">
        <div class="info-item">
          <span class="info-label">ID:</span>
          <span class="info-value">{{ userInfo.id }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">用户名:</span>
          <span class="info-value">{{ userInfo.name || user?.username || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">账号ID:</span>
          <span class="info-value">{{ userInfo.aid }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">性别:</span>
          <span class="info-value">{{ userInfo.sex || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">年龄:</span>
          <span class="info-value">{{ userInfo.age || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">注册时间:</span>
          <span class="info-value">{{ formattedRegisterTime }}</span>
        </div>
        <div class="info-item info-text">
          <span class="info-label">个人简介:</span>
          <span class="info-value">{{ userInfo.text || '未设置' }}</span>
        </div>
        
        <div v-if="userInfo.imgBase64" class="user-avatar">
          <h3>用户头像</h3>
          <img :src="`data:image/jpeg;base64,${userInfo.imgBase64}`" alt="用户头像" />
        </div>
      </div>
      
      <div v-else class="empty-info">
        <p>未获取到用户信息</p>
        <el-button type="primary" @click="refreshUserInfo" class="refresh-button">
          获取信息
        </el-button>
      </div>
      
      <div class="action-buttons">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="refreshUserInfo" :loading="loading">
          刷新信息
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-info-container {
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

.user-info-box {
  background: white;
  width: 500px;
  max-height: 80vh;
  padding: 30px;
  border-radius: 8px;
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow-y: auto;
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

.loading-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  color: #909399;
}

.error-message {
  margin: 20px 0;
}

.user-info-content {
  margin: 20px 0;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
}

.info-label {
  font-weight: bold;
  width: 100px;
  color: #606266;
}

.info-value {
  flex: 1;
  color: #303133;
}

.info-text {
  align-items: flex-start;
}

.user-avatar {
  margin-top: 20px;
  text-align: center;
}

.user-avatar img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 50%;
  border: 1px solid #eee;
}

.empty-info {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
  gap: 10px;
}

.refresh-button {
  margin-top: 15px;
}
</style> 