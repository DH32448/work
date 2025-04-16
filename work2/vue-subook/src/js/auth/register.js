import { ElMessage } from 'element-plus'

/**
 * 处理注册表单提交
 * @param {Object} registerForm - 注册表单数据
 * @param {Function} onSuccess - 注册成功回调
 * @param {Function} onFail - 注册失败回调
 * @param {Function} onFinish - 注册处理完成回调
 */
export async function handleRegisterSubmit(registerForm, onSuccess, onFail, onFinish) {
  // 模拟异步注册请求
  setTimeout(() => {
    // 模拟已存在用户名检查
    const existingUsers = ['admin', 'test']
    
    if (existingUsers.includes(registerForm.username)) {
      // 用户名已存在
      ElMessage.error('用户名已被注册')
      onFail && onFail('用户名已被注册')
    } else if (registerForm.captcha !== '1234') {
      // 验证码错误
      ElMessage.error('验证码错误')
      onFail && onFail('验证码错误')
    } else {
      // 注册成功
      ElMessage.success('注册成功！请登录使用')
      
      // 构造用户数据（实际应用中由后端返回）
      const userData = {
        username: registerForm.username,
        email: registerForm.email,
        createdAt: new Date().toISOString()
      }
      
      // 调用成功回调
      onSuccess && onSuccess(userData)
    }
    
    // 完成回调
    onFinish && onFinish()
  }, 1000)
}

/**
 * 获取注册表单校验规则
 * @param {Object} formData - 表单数据对象
 * @returns {Object} 表单校验规则
 */
export function getRegisterRules(formData) {
  return {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '长度应在 3 到 20 个字符之间', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '长度应在 6 到 20 个字符之间', trigger: 'blur' },
      { 
        validator: (rule, value, callback) => {
          if (!value) {
            callback()
          } else if (!/[A-Z]/.test(value)) {
            callback(new Error('密码需包含至少一个大写字母'))
          } else if (!/[0-9]/.test(value)) {
            callback(new Error('密码需包含至少一个数字'))
          } else if (!/[\W_]/.test(value)) {
            callback(new Error('密码需包含至少一个特殊字符'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    confirmPassword: [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { 
        validator: (rule, value, callback) => {
          if (value === '') {
            callback(new Error('请再次输入密码'))
          } else if (value !== formData.password) {
            callback(new Error('两次输入密码不一致'))
          } else {
            callback()
          }
        }, 
        trigger: 'blur' 
      }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { len: 4, message: '验证码长度应为4位', trigger: 'blur' }
    ],
    agreement: [
      { 
        validator: (rule, value, callback) => {
          if (!value) {
            callback(new Error('请阅读并同意用户协议'))
          } else {
            callback()
          }
        }, 
        trigger: 'change' 
      }
    ]
  }
}

/**
 * 检查用户名是否可用
 * @param {string} username - 要检查的用户名
 * @returns {Promise<boolean>} 用户名是否可用
 */
export function checkUsernameAvailability(username) {
  return new Promise((resolve) => {
    setTimeout(() => {
      // 模拟已存在用户名检查
      const existingUsers = ['admin', 'test']
      resolve(!existingUsers.includes(username))
    }, 500)
  })
}

/**
 * 计算密码强度
 * @param {string} password - 密码
 * @returns {Object} 密码强度信息
 */
export function calculatePasswordStrength(password) {
  if (!password) {
    return { score: 0, level: 'empty', message: '请输入密码' }
  }
  
  let score = 0
  
  // 长度得分
  if (password.length >= 6) score += 1
  if (password.length >= 10) score += 1
  
  // 复杂度得分
  if (/[A-Z]/.test(password)) score += 1  // 大写字母
  if (/[a-z]/.test(password)) score += 1  // 小写字母
  if (/[0-9]/.test(password)) score += 1  // 数字
  if (/[\W_]/.test(password)) score += 1  // 特殊字符
  
  // 根据得分确定强度级别
  let level, message, color
  
  if (score < 3) {
    level = 'weak'
    message = '弱密码'
    color = '#F56C6C' // 红色
  } else if (score < 5) {
    level = 'medium'
    message = '中等强度'
    color = '#E6A23C' // 黄色
  } else {
    level = 'strong'
    message = '强密码'
    color = '#67C23A' // 绿色
  }
  
  return { score, level, message, color }
}

/**
 * 生成验证码
 * @returns {string} 4位验证码
 */
export function generateCaptcha() {
  // 在实际应用中，验证码应由服务器生成
  // 这里仅作演示，返回固定值 "1234"
  return "1234"
} 