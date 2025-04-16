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
    console.log('开始请求验证码，邮箱:', email);
    const response = await request({
      url: '/api/auth/ask-code',
      method: 'get',
      params: { email }
    });
    
    console.log('验证码响应完整内容:', response);
    
    // 检查响应格式，确保正确解析
    if (response.data && (response.data.code === 200 || response.data.code === 0)) {
      return {
        success: true,
        message: response.data.data || response.data.message || '验证码发送成功'
      };
    } else {
      console.warn('验证码响应格式异常:', response.data);
      return {
        success: false,
        message: response.data?.message || '获取验证码失败'
      };
    }
  } catch (error) {
    console.error('获取验证码失败，详细错误:', error);
    console.error('错误响应:', error.response?.data);
    console.error('错误状态:', error.response?.status);
    throw error;
  }
};

export const register = async (username, email, phone, code, password) => {
  try {
    // 创建表单数据以匹配后端@RequestParam注解
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('email', email);
    formData.append('phone', phone);
    formData.append('code', code);
    formData.append('password', password);
    
    console.log('注册请求数据:', formData.toString());
    
    const response = await request({
      url: '/api/auth/register',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
    
    console.log('注册响应完整内容:', response);
    
    // 检查响应格式，确保正确解析
    if (response.data && (response.data.code === 200 || response.data.code === 0)) {
      return {
        success: true,
        message: response.data.data || response.data.message || '注册成功'
      };
    } else {
      console.warn('注册响应格式异常:', response.data);
      return {
        success: false,
        message: response.data?.message || '注册失败'
      };
    }
  } catch (error) {
    console.error('注册失败详细错误:', error);
    console.error('错误响应:', error.response?.data);
    console.error('错误状态:', error.response?.status);
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
    let userData = { id: 0, username: username, role: 'user', email: '' };
    
    // 如果token是JWT格式，从中提取用户信息
    if (token.split('.').length === 3) {
      try {
        const tokenData = JSON.parse(atob(token.split('.')[1]));
        userData = {
          id: tokenData.id || 0,
          username: tokenData.sub || username,
          role: tokenData.role || tokenData.authorities || 'user',
          email: tokenData.email || ''
        };
      } catch (e) {
        console.warn('无法解析JWT token，使用默认用户信息', e);
      }
    }
    
    // 保存用户基本信息到本地存储
    localStorage.setItem('user', JSON.stringify(userData));
    
    // 尝试获取用户详细信息
    try {
      // 使用用户名作为参数获取用户详细信息
      // 注意：如果后端要求使用邮箱或手机号，可能需要调整
      const userInfoResponse = await getUserInfo(userData.email || username);
      
      if (userInfoResponse && userInfoResponse.success) {
        console.log('成功获取到用户详细信息:', userInfoResponse.data);
      } else {
        console.warn('获取用户详细信息失败:', userInfoResponse?.message);
      }
    } catch (infoError) {
      console.error('获取用户详细信息出错:', infoError);
      // 这里我们不阻止登录流程，即使获取详细信息失败
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

/**
 * 获取用户详细信息
 * @param {string} phoneOrEmail - 用户的手机号或邮箱
 * @returns {Promise<Object>} - 用户详细信息
 */
export const getUserInfo = async (phoneOrEmail) => {
  try {
    console.log('开始请求用户信息，账号:', phoneOrEmail);
    
    const response = await request({
      url: '/api/auth/info',
      method: 'get',
      params: { info: phoneOrEmail }
    });
    
    console.log('用户信息响应完整内容:', response);
    
    // 检查响应格式，确保正确解析
    if (response.data && (response.data.code === 200 || response.data.code === 0)) {
      // 存储用户详细信息到本地存储
      if (response.data.data) {
        const userInfo = response.data.data;
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
      }
      
      return {
        success: true,
        data: response.data.data || {},
        message: response.data.message || '获取用户信息成功'
      };
    } else {
      console.warn('用户信息响应格式异常:', response.data);
      return {
        success: false,
        data: null,
        message: response.data?.message || '获取用户信息失败'
      };
    }
  } catch (error) {
    console.error('获取用户信息失败，详细错误:', error);
    console.error('错误响应:', error.response?.data);
    console.error('错误状态:', error.response?.status);
    throw error;
  }
};

/**
 * 更新用户信息（包括头像）
 * @param {Object} userInfo - 用户信息对象
 * @param {number} userInfo.aid - 用户账号ID
 * @param {string} [userInfo.name] - 用户名
 * @param {number} [userInfo.age] - 年龄
 * @param {string} [userInfo.sex] - 性别
 * @param {string} [userInfo.text] - 个人简介
 * @param {File} [userInfo.image] - 头像图片文件
 * @returns {Promise<Object>} - 更新结果
 */
export const updateUserInfo = async (userInfo) => {
  try {
    console.log('开始更新用户信息:', userInfo);
    
    // 创建表单数据
    const formData = new FormData();
    formData.append('aid', userInfo.aid);
    
    // 仅添加存在的字段
    if (userInfo.name !== undefined) {
      formData.append('name', userInfo.name);
    }
    
    if (userInfo.age !== undefined) {
      formData.append('age', userInfo.age);
    }
    
    if (userInfo.sex !== undefined) {
      formData.append('sex', userInfo.sex);
    }
    
    if (userInfo.text !== undefined) {
      formData.append('text', userInfo.text);
    }
    
    // 如果有图片文件，添加到表单
    if (userInfo.image && userInfo.image instanceof File) {
      formData.append('image', userInfo.image);
    }
    
    const response = await request({
      url: '/api/auth/update-info-with-image',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    console.log('更新用户信息响应:', response);
    
    // 检查响应格式，确保正确解析
    if (response.data && (response.data.code === 200 || response.data.code === 0)) {
      return {
        success: true,
        message: response.data.data || response.data.message || '更新成功'
      };
    } else {
      console.warn('更新用户信息响应格式异常:', response.data);
      return {
        success: false,
        message: response.data?.message || '更新失败'
      };
    }
  } catch (error) {
    console.error('更新用户信息失败，详细错误:', error);
    console.error('错误响应:', error.response?.data);
    console.error('错误状态:', error.response?.status);
    throw error;
  }
}; 