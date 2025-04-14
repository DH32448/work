import axios from 'axios';

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
      // 模拟返回token
      const token = btoa(JSON.stringify({
        id: user.id,
        username: user.username,
        role: user.role,
        exp: Date.now() + 24 * 60 * 60 * 1000 // 24小时后过期
      }));
      
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
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 300));
    
    // 清除本地存储的认证信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    return true;
  } catch (error) {
    console.error('退出登录失败:', error);
    throw error;
  }
};

export const checkAuth = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('未登录');
    }
    
    // 解析token
    const tokenData = JSON.parse(atob(token));
    
    // 检查是否过期
    if (tokenData.exp < Date.now()) {
      throw new Error('登录已过期');
    }
    
    return {
      isAuthenticated: true,
      user: {
        id: tokenData.id,
        username: tokenData.username,
        role: tokenData.role
      }
    };
  } catch (error) {
    console.error('验证登录状态失败:', error);
    return {
      isAuthenticated: false,
      user: null
    };
  }
}; 