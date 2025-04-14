import axios from 'axios';
import qs from 'qs';

// 创建axios实例
const instance = axios.create({
  baseURL: '',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    // 处理错误响应
    return Promise.reject(error);
  }
);

// GET请求
export const $get = (url, params) => {
  return instance.get(url, { params });
};

// POST请求 - application/x-www-form-urlencoded
export const $postx = (url, data) => {
  return instance.post(url, qs.stringify(data), {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  });
};

// POST请求 - application/json
export const $postj = (url, data) => {
  return instance.post(url, data);
};

// POST请求 - multipart/form-data
export const $postf = (url, formData) => {
  return instance.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export default instance; 