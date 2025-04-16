import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, getOrderStats, getRecentlyViewed, updateUserProfile } from '../user/profile'

/**
 * 个人中心页面组合式API
 * @param {Object} userData 用户基础数据
 * @returns {Object} 个人中心相关的状态和方法
 */
export function useProfile(userData) {
  // 用户详细信息
  const userInfo = ref(getUserProfile(userData))
  
  // 订单统计
  const orderStats = ref(getOrderStats())
  
  // 最近浏览
  const recentlyViewed = ref(getRecentlyViewed())
  
  // 是否正在编辑
  const isEditing = ref(false)
  
  // 是否正在保存
  const isSaving = ref(false)
  
  // 个人信息表单
  const profileForm = reactive({
    nickname: userInfo.value.username || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    address: userInfo.value.address || '',
    birthday: userInfo.value.birthday || ''
  })
  
  // 表单验证规则
  const rules = {
    nickname: [
      { required: true, message: '请输入昵称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    phone: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ]
  }
  
  // 会员标签颜色
  const levelColor = computed(() => {
    const level = userInfo.value.level || 0
    if (level >= 4) return '#FF9800'  // 钻石
    if (level >= 3) return '#409EFF'  // 铂金
    if (level >= 2) return '#67C23A'  // 黄金
    if (level >= 1) return '#909399'  // 白银
    return '#C0C4CC'                 // 普通
  })
  
  /**
   * 开始编辑个人资料
   */
  const startEditing = () => {
    isEditing.value = true
  }
  
  /**
   * 取消编辑
   */
  const cancelEditing = () => {
    isEditing.value = false
    // 重置表单为当前用户信息
    Object.assign(profileForm, {
      nickname: userInfo.value.username || '',
      email: userInfo.value.email || '',
      phone: userInfo.value.phone || '',
      address: userInfo.value.address || '',
      birthday: userInfo.value.birthday || ''
    })
  }
  
  /**
   * 保存个人资料
   */
  const saveProfile = async (formEl) => {
    if (!formEl) return
    
    try {
      await formEl.validate()
      
      isSaving.value = true
      
      // 调用更新接口
      const res = await updateUserProfile(profileForm)
      
      if (res.success) {
        // 更新本地用户信息
        userInfo.value = {
          ...userInfo.value,
          username: profileForm.nickname,
          email: profileForm.email,
          phone: profileForm.phone,
          address: profileForm.address,
          birthday: profileForm.birthday
        }
        
        // 关闭编辑模式
        isEditing.value = false
      }
    } catch (error) {
      console.error('表单验证失败', error)
      ElMessage.error('请检查表单填写是否正确')
    } finally {
      isSaving.value = false
    }
  }
  
  return {
    userInfo,
    orderStats,
    recentlyViewed,
    isEditing,
    isSaving,
    profileForm,
    rules,
    levelColor,
    startEditing,
    cancelEditing,
    saveProfile
  }
} 