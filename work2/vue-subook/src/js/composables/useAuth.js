import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { saveUserData, getUserData, removeUserData } from '../auth/authStorage'

/**
 * 认证状态管理组合式API
 * @returns {Object} 认证相关的状态和方法
 */
export function useAuth() {
  // 登录状态
  const isLoggedIn = ref(false)
  // 用户信息
  const userInfo = ref(null)
  // 登录对话框显示状态
  const loginDialogVisible = ref(false)
  
  /**
   * 显示登录对话框
   */
  const showLoginDialog = () => {
    loginDialogVisible.value = true
  }
  
  /**
   * 处理登录成功
   * @param {Object} userData 用户数据
   */
  const handleLoginSuccess = (userData) => {
    // 显示欢迎信息
    ElMessage({
      message: `欢迎回来，${userData.username}！`,
      type: 'success',
      duration: 3000
    })
    
    // 更新登录状态
    isLoggedIn.value = true
    userInfo.value = userData
    
    // 存储登录信息到本地存储
    saveUserData(userData)
  }
  
  /**
   * 退出登录
   */
  const logout = () => {
    isLoggedIn.value = false
    userInfo.value = null
    
    // 清除本地存储的登录信息
    removeUserData()
    
    // 显示退出登录提示
    ElMessage({
      message: '您已成功退出登录',
      type: 'info',
      duration: 2000
    })
  }
  
  /**
   * 检查是否已登录
   */
  const checkLoginStatus = () => {
    const userData = getUserData()
    if (userData) {
      userInfo.value = userData
      isLoggedIn.value = true
    }
  }
  
  // 初始化时检查登录状态
  checkLoginStatus()
  
  return {
    isLoggedIn,
    userInfo,
    loginDialogVisible,
    showLoginDialog,
    handleLoginSuccess,
    logout
  }
} 