<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserInfo, updateUserInfo } from '../api/auth';

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
// 是否处于编辑模式
const isEditing = ref(false);
// 保存加载状态
const saveLoading = ref(false);

// 编辑表单
const editForm = reactive({
  name: '',
  age: '',
  sex: '',
  text: '',
  image: null
});

// 图片预览URL
const imagePreviewUrl = ref('');

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
  // 如果正在编辑，提示用户是否确定放弃编辑
  if (isEditing.value) {
    if (confirm('确定放弃编辑内容吗？')) {
      isEditing.value = false;
      emit('close');
    }
  } else {
    emit('close');
  }
};

// 刷新用户信息
const refreshUserInfo = () => {
  fetchUserInfo();
};

// 切换到编辑模式
const startEditing = () => {
  if (!userInfo.value) {
    ElMessage.warning('请先获取用户信息');
    return;
  }
  
  // 初始化编辑表单
  editForm.name = userInfo.value.name || '';
  editForm.age = userInfo.value.age || '';
  editForm.sex = userInfo.value.sex || '';
  editForm.text = userInfo.value.text || '';
  editForm.image = null;
  
  // 如果有头像图片，设置预览URL
  if (userInfo.value.imgBase64) {
    imagePreviewUrl.value = `data:image/jpeg;base64,${userInfo.value.imgBase64}`;
  } else {
    imagePreviewUrl.value = '';
  }
  
  isEditing.value = true;
};

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false;
  editForm.image = null;
  imagePreviewUrl.value = userInfo.value.imgBase64 
    ? `data:image/jpeg;base64,${userInfo.value.imgBase64}` 
    : '';
};

// 处理图片选择
const handleImageChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请选择有效的图片文件（JPEG, PNG, GIF）');
    return;
  }
  
  // 验证文件大小（不超过2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }
  
  // 保存文件引用
  editForm.image = file;
  
  // 创建预览URL
  const reader = new FileReader();
  reader.onload = (e) => {
    imagePreviewUrl.value = e.target.result;
  };
  reader.readAsDataURL(file);
};

// 删除选择的图片
const removeImage = () => {
  editForm.image = null;
  if (userInfo.value.imgBase64) {
    imagePreviewUrl.value = `data:image/jpeg;base64,${userInfo.value.imgBase64}`;
  } else {
    imagePreviewUrl.value = '';
  }
};

// 保存用户信息
const saveUserInfo = async () => {
  if (!userInfo.value || !userInfo.value.aid) {
    ElMessage.error('无法获取用户账号ID，无法更新信息');
    return;
  }
  
  saveLoading.value = true;
  
  try {
    const updateData = {
      aid: userInfo.value.aid,
      name: editForm.name,
      age: editForm.age || undefined,
      sex: editForm.sex,
      text: editForm.text,
      image: editForm.image
    };
    
    const response = await updateUserInfo(updateData);
    
    if (response && response.success) {
      ElMessage.success(response.message || '更新信息成功');
      isEditing.value = false;
      
      // 重新获取用户信息
      await fetchUserInfo();
      
      // 通知父组件更新成功
      emit('update');
    } else {
      ElMessage.error(response.message || '更新信息失败');
    }
  } catch (error) {
    console.error('保存用户信息出错:', error);
    ElMessage.error(error.message || '更新信息时发生错误');
  } finally {
    saveLoading.value = false;
  }
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
      <h2>{{ isEditing ? '编辑个人信息' : '用户信息' }}</h2>
      
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
      
      <div v-else-if="userInfo">
        <!-- 查看模式 -->
        <div v-if="!isEditing" class="user-info-content">
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
          
          <div class="action-buttons">
            <el-button @click="handleClose">关闭</el-button>
            <el-button type="primary" @click="refreshUserInfo" :loading="loading">
              刷新信息
            </el-button>
            <el-button type="success" @click="startEditing">
              编辑信息
            </el-button>
          </div>
        </div>
        
        <!-- 编辑模式 -->
        <div v-else class="edit-form">
          <el-form label-position="top">
            <el-form-item label="用户名">
              <el-input v-model="editForm.name" placeholder="请输入用户名"></el-input>
            </el-form-item>
            
            <el-form-item label="性别">
              <el-select v-model="editForm.sex" placeholder="请选择性别" class="full-width">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
                <el-option label="保密" value="保密"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="年龄">
              <el-input-number 
                v-model="editForm.age" 
                :min="0" 
                :max="120" 
                class="full-width">
              </el-input-number>
            </el-form-item>
            
            <el-form-item label="个人简介">
              <el-input 
                v-model="editForm.text" 
                type="textarea" 
                rows="4" 
                placeholder="请输入个人简介">
              </el-input>
            </el-form-item>
            
            <el-form-item label="用户头像">
              <div class="avatar-uploader">
                <div v-if="imagePreviewUrl" class="avatar-preview">
                  <img :src="imagePreviewUrl" alt="头像预览" />
                  <div class="avatar-actions">
                    <el-button type="danger" size="small" @click="removeImage">删除</el-button>
                  </div>
                </div>
                <div v-else class="avatar-upload-box">
                  <input
                    type="file"
                    accept="image/jpeg,image/png,image/gif"
                    @change="handleImageChange"
                    class="file-input"
                    id="avatar-upload"
                  />
                  <label for="avatar-upload" class="upload-label">
                    <i class="el-icon-plus"></i>
                    <span>点击上传头像</span>
                  </label>
                </div>
              </div>
              <div class="upload-tip">
                支持JPG、PNG、GIF格式，大小不超过2MB
              </div>
            </el-form-item>
          </el-form>
          
          <div class="action-buttons">
            <el-button @click="cancelEditing">取消</el-button>
            <el-button 
              type="primary" 
              @click="saveUserInfo" 
              :loading="saveLoading">
              保存修改
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-info">
        <p>未获取到用户信息</p>
        <el-button type="primary" @click="refreshUserInfo" class="refresh-button">
          获取信息
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
  max-height: 90vh;
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

/* 编辑表单样式 */
.edit-form {
  margin: 20px 0;
}

.full-width {
  width: 100%;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.avatar-preview {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 8px 0;
  display: flex;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-preview:hover .avatar-actions {
  opacity: 1;
}

.avatar-upload-box {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 1px dashed #d9d9d9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-upload-box:hover {
  border-color: #409EFF;
}

.file-input {
  display: none;
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  cursor: pointer;
  color: #8c939d;
}

.upload-label i {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  text-align: center;
  margin-top: 5px;
}
</style> 