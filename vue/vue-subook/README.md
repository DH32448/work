# Su Book 项目

基于Vue 3、Element Plus、Pinia和Axios构建的前端项目。

## 项目特点

- 使用Vue 3的Composition API
- 使用Element Plus组件库
- 使用Pinia进行状态管理
- 使用Vue Router进行路由管理
- 使用Axios进行API请求
- 使用Vite作为构建工具

## 安装依赖

```bash
npm install
```

## 开发模式启动

```bash
npm run dev
```

## 构建生产版本

```bash
npm run build
```

## 项目结构

```
|- src/              # 源代码
|  |- api/           # API接口
|  |- assets/        # 静态资源
|  |- components/    # 组件
|  |- router/        # 路由
|  |- stores/        # Pinia状态
|  |- utils/         # 工具函数
|  |- views/         # 页面
|  |- App.vue        # 主组件
|  |- main.js        # 入口文件
|- public/           # 公共资源
|- .env              # 环境变量
|- index.html        # HTML模板
|- vite.config.js    # Vite配置
``` 