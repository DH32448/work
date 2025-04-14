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
    role: 'admin'
  },
  {
    id: 2,
    username: 'user',
    password: '123456',
    role: 'user'
  }
];

export const login = async (username, password) => {
  try {
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const user = mockUsers.find(u => 
      u.username === username && u.password === password
    );
    
    if (user) {
      // 生成有效的token
      // 构造header部分（通常包含算法和token类型）
      const header = {
        alg: 'HS256',
        typ: 'JWT'
      };
      
      // 当前时间（以秒为单位）
      const now = Math.floor(Date.now() / 1000);
      
      // 构造payload部分
      const payload = {
        id: user.id,
        username: user.username,
        role: user.role,
        iat: now,  // 签发时间
        exp: now + 24 * 60 * 60  // 24小时后过期（以秒为单位）
      };
      
      // 编码header和payload
      const encodedHeader = btoa(JSON.stringify(header));
      const encodedPayload = btoa(JSON.stringify(payload));
      
      // 简化的签名（实际生产环境应该使用加密库生成真正的签名）
      const signature = btoa('signature');
      
      // 组合token（格式为 header.payload.signature）
      const token = `${encodedHeader}.${encodedPayload}.${signature}`;
      
      return {
        token,
        user: {
          id: user.id,
          username: user.username,
          role: user.role
        }
      };
    } else {
      throw new Error('用户名或密码错误');
    }
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

export const logout = async () => {
  try {
    const response = await request({
      url: '/api/auth/logout',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    // 清除本地存储的认证信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    return response.data;
  } catch (error) {
    console.error('退出登录失败:', error);
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