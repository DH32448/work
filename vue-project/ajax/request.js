import axios from 'axios'
import router from '../router'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 保持基础URL，但确保前端API路径与后端匹配
  timeout: 10000, // 请求超时时间
  withCredentials: true, // 允许跨域请求携带cookies
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 处理不同类型的Content-Type
    const contentType = config.headers['Content-Type'] || config.headers['content-type'];
    
    // 如果是application/x-www-form-urlencoded类型，确保数据正确转换
    if (contentType && contentType.includes('application/x-www-form-urlencoded') && 
        config.data && !(config.data instanceof URLSearchParams)) {
      // 如果data是对象但不是URLSearchParams，转换为URLSearchParams
      if (typeof config.data === 'object') {
        const formData = new URLSearchParams();
        Object.keys(config.data).forEach(key => {
          formData.append(key, config.data[key]);
        });
        config.data = formData;
      }
    }
    
    // 打印请求信息以便调试
    console.log('发送请求：', config.method, config.url, config.params || config.data);
    
    return config
  },
  error => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 打印响应信息以便调试
    console.log('收到响应：', response.status, response.data);
    
    // 特殊处理登录请求的响应
    if (response.config.url.includes('/api/auth/login')) {
      // Spring Security默认情况下会设置名为Authorization的响应头
      const authHeader = response.headers['authorization'];
      if (authHeader && authHeader.startsWith('Bearer ')) {
        const token = authHeader.substring(7);
        localStorage.setItem('token', token);
        
        // 模拟一个标准响应格式供前端代码使用
        if (!response.data || typeof response.data !== 'object') {
          response.data = {
            code: 0,
            message: '登录成功',
            data: token
          };
        } else if (!response.data.token && !response.data.data) {
          response.data.data = token;
        }
      }
    }
    
    // 如果响应中包含新的 token，更新本地存储
    if (response.data?.token) {
      localStorage.setItem('token', response.data.token);
    }
    
    // 确保返回标准格式的响应数据
    if (response.data && typeof response.data === 'string') {
      try {
        response.data = JSON.parse(response.data);
      } catch (e) {
        console.warn('响应数据不是有效的JSON格式:', response.data);
        response.data = {
          code: 200,
          data: response.data,
          message: '请求成功'
        };
      }
    }
    
    // 标准化响应数据格式，确保前端各处可以统一处理
    if (response.data && typeof response.data === 'object') {
      // 如果响应中没有code字段，添加默认值
      if (response.data.code === undefined) {
        response.data.code = response.status === 200 ? 200 : 500;
      }
      
      // 如果响应中没有message字段，添加默认值
      if (response.data.message === undefined) {
        response.data.message = response.status === 200 ? '请求成功' : '请求失败';
      }
    }
    
    return response;
  },
  error => {
    console.error('请求错误：', error.response?.status, error.response?.data || error.message);
    
    // 特殊处理登录请求的错误
    if (error.config.url.includes('/api/auth/login')) {
      if (error.response?.status === 401) {
        ElMessage.error('用户名或密码错误');
      } else {
        ElMessage.error('登录请求失败，请稍后再试');
      }
    }
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // token 过期或无效，清除 token 并跳转到登录页
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          ElMessage.error('登录已过期，请重新登录');
          router.push('/login');
          break;
        case 403:
          // 权限不足
          ElMessage.error('权限不足');
          router.push('/403');
          break;
        case 500:
          // 服务器错误
          ElMessage.error(error.response.data?.message || '服务器错误，请稍后重试');
          break;
        default:
          ElMessage.error(error.response.data?.message || '请求失败，请稍后重试');
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接');
    }
    return Promise.reject(error);
  }
)

export default service 