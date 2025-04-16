import { ElMessage } from 'element-plus'

/**
 * 登录相关的验证规则和处理函数
 */

/**
 * 获取登录表单验证规则
 * @returns {Object} 登录表单验证规则
 */
export function getLoginRules() {
  return {
    username: [
      { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
      { min: 3, max: 30, message: '长度在3到30个字符之间', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 30, message: '长度在6到30个字符之间', trigger: 'blur' }
    ],
    captcha: [
      { 
        required: true, 
        message: '请输入验证码', 
        trigger: 'blur',
        // 仅当验证码需要显示时验证
        validator: (rule, value, callback, source, options) => {
          const form = options.getForm()
          if (form && form.captchaRequired && !value) {
            callback(new Error('请输入验证码'))
          } else {
            callback()
          }
        }
      },
      { min: 4, max: 4, message: '验证码必须是4位字符', trigger: 'blur' }
    ]
  }
}

/**
 * 处理登录表单提交
 * @param {Object} loginForm 登录表单数据
 * @param {Function} onSuccess 成功回调
 * @param {Function} onError 错误回调
 * @param {Function} onComplete 完成回调（不论成功失败）
 */
export function handleLoginSubmit(loginForm, onSuccess, onError, onComplete) {
  // 这里是模拟的登录逻辑，实际项目中应当替换为真实的API调用
  setTimeout(() => {
    // 模拟登录逻辑
    const { username, password, captcha } = loginForm
    
    // 用于测试的账号信息
    const testUser = {
      username: 'admin',
      password: '123456',
      email: 'admin@example.com'
    }
    
    // 模拟登录验证
    if (username === testUser.username && password === testUser.password) {
      // 登录成功
      const userData = {
        id: 1,
        username: testUser.username,
        email: testUser.email,
        nickname: '管理员',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        token: 'mock_token_' + Math.random().toString(36).substr(2)
      }
      
      // 如果设置了记住我，可以在这里处理存储用户信息到本地存储
      if (loginForm.rememberMe) {
        localStorage.setItem('rememberUser', JSON.stringify({
          username: userData.username
        }))
      } else {
        localStorage.removeItem('rememberUser')
      }
      
      // 调用成功回调
      onSuccess && onSuccess(userData)
    } else {
      // 登录失败
      let errorMsg = '用户名或密码错误'
      
      // 随机模拟需要验证码的情况
      if (Math.random() > 0.7) {
        errorMsg = '密码错误次数过多，请输入验证码'
      }
      
      // 调用错误回调
      onError && onError(errorMsg)
    }
    
    // 调用完成回调
    onComplete && onComplete()
  }, 1000) // 模拟网络延迟
} 