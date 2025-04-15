import axios from 'axios';
import request from '../../ajax/request';

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
});

// 模拟用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    password: '123456',
    email: 'admin@example.com',
    role: 'admin'
  },
  {
    id: 2,
    username: 'user',
    password: '123456',
    email: 'user@example.com',
    role: 'user'
  }
];

export const getVerificationCode = async (email) => {
  try {
    const response = await request({
      url: '/api/auth/ask-code',
      method: 'get',
      params: { email }
    });
    
    return response.data;
  } catch (error) {
    console.error('获取验证码失败:', error);
    throw error;
  }
};

export const register = async (username, email, phone, code, password) => {
  try {
    const response = await request({
      url: '/api/auth/register',
      method: 'post',
      params: {
        username,
        email,
        phone,
        code,
        password
      }
    });
    
    return {
      success: response.data.code === 0,
      message: response.data.message
    };
  } catch (error) {
    console.error('注册失败:', error);
    throw error;
  }
};

export const login = async (username, password) => {
  try {
    // 创建表单数据
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);
    
    // 发送请求到Spring Security表单登录端点
    const response = await request({
      url: '/api/auth/login',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    // 获取token - 可能来自响应数据或响应已在request拦截器中处理
    let token = '';
    if (response.data && response.data.data) {
      token = response.data.data;
    } else if (response.data && response.data.token) {
      token = response.data.token;
    } else {
      token = localStorage.getItem('token');
    }
    
    if (!token) {
      throw new Error('登录成功但未能获取有效的令牌');
    }
    
    // 尝试解析token获取用户信息
    let userData = { id: 0, username: username, role: 'user' };
    
    // 如果token是JWT格式，从中提取用户信息
    if (token.split('.').length === 3) {
      try {
        const tokenData = JSON.parse(atob(token.split('.')[1]));
        userData = {
          id: tokenData.id || 0,
          username: tokenData.sub || username,
          role: tokenData.role || tokenData.authorities || 'user'
        };
      } catch (e) {
        console.warn('无法解析JWT token，使用默认用户信息', e);
      }
    }
    
    return {
      token,
      user: userData
    };
  } catch (error) {
    console.error('登录失败:', error);
    // 如果是401错误，说明用户名或密码错误
    if (error.response && error.response.status === 401) {
      throw new Error('用户名或密码错误');
    }
    throw error;
  }
};

export const logout = async () => {
  try {
    // 发送请求到Spring Security配置的注销URL
    const response = await request({
      url: '/api/auth/logout',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    // 无论后端响应如何，都清除前端存储的认证信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    return {
      success: true,
      message: '退出登录成功'
    };
  } catch (error) {
    console.error('退出登录失败:', error);
    
    // 即使请求失败，也清除本地存储的令牌信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    throw error;
  }
};

export const checkAuth = async () => {
  try {
    const token = localStorage.getItem('token');
    const userString = localStorage.getItem('user');
    
    if (!token || !userString) {
      return {
        isAuthenticated: false,
        user: null
      };
    }
    
    try {
      // 解析token
      const tokenData = JSON.parse(atob(token.split('.')[1]));
      
      // 检查是否过期
      if (tokenData.exp < Date.now() / 1000) {
        // 过期了，清除token
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        return {
          isAuthenticated: false,
          user: null
        };
      }
      
      // token有效，获取用户信息
      const user = JSON.parse(userString);
      
      return {
        isAuthenticated: true,
        user
      };
    } catch (error) {
      // token格式不正确，清除token
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return {
        isAuthenticated: false,
        user: null
      };
    }
  } catch (error) {
    console.error('验证登录状态失败:', error);
    return {
      isAuthenticated: false,
      user: null
    };
  }
}; 