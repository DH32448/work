# SuBook项目目录结构说明

本文档介绍SuBook项目的目录结构及各部分的作用，帮助开发人员快速了解项目架构。

## 项目概述

SuBook是一个基于Spring Boot的Web应用，采用了经典的三层架构（控制层、服务层、数据访问层）设计。项目集成了Spring Security、Redis、MyBatis-Plus等主流框架，实现了用户认证、图片上传等功能。

## 目录结构

```
subook/
├── .idea/                    # IntelliJ IDEA配置文件
├── .mvn/                     # Maven包装器目录
├── src/                      # 源代码目录
│   ├── main/                 # 主要源代码
│   │   ├── java/             # Java源代码
│   │   │   └── com/example/  # 项目主包
│   │   │       ├── config/   # 配置类
│   │   │       ├── controller/ # 控制器层
│   │   │       ├── entity/   # 实体类
│   │   │       │   ├── dto/  # 数据传输对象
│   │   │       │   ├── ot/   # 其他类型对象
│   │   │       │   └── vo/   # 视图对象
│   │   │       ├── filter/   # 过滤器
│   │   │       ├── mapper/   # MyBatis映射接口
│   │   │       ├── service/  # 服务层接口
│   │   │       │   └── impl/ # 服务层实现
│   │   │       ├── utils/    # 工具类
│   │   │       └── SubookApplication.java # 应用入口类
│   │   └── resources/        # 资源文件目录
│   │       ├── application.yml # 主配置文件
│   │       ├── application-dev.yml # 开发环境配置
│   │       └── application-dev2.yml # 备用开发环境配置
│   └── test/                 # 测试代码目录
├── mysql.sql                 # 数据库初始化脚本
└── pom.xml                   # Maven项目依赖配置文件
```

## 主要目录说明

### 1. config目录

配置类目录，包含应用的各种配置Bean：

- `RedisConfig.java`: Redis配置类，配置RedisTemplate的序列化方式
- `MultipartConfig.java`: 文件上传配置
- `SecurityConfig.java`: Spring Security安全配置
- `GlobalExceptionHandler.java`: 全局异常处理器

### 2. controller目录

控制器层，处理HTTP请求，调用服务层处理业务逻辑：

- `AdmController.java`: 管理员相关功能控制器
- `AuthController.java`: 认证相关功能控制器

### 3. entity目录

实体类目录，包含数据库映射实体和各种传输对象：

#### 3.1 entity/dto目录

数据传输对象(Data Transfer Object)，用于层间数据传输：

- `AccountDetails.java`: 账户详细信息DTO
- `AccountInfoDTO.java`: 账户信息DTO，包含Base64编码的图片数据

#### 3.2 entity/vo目录

视图对象(View Object)，用于前端展示：

- `RestBean.java`: REST API响应包装类

#### 3.3 entity/ot目录

其他类型的对象，如枚举类等

### 4. mapper目录

MyBatis-Plus的Mapper接口目录，定义数据访问操作

### 5. service目录

服务层接口，定义业务逻辑操作：

- `AccountDetailsService.java`: 账户详情服务
- `AccountInfoService.java`: 账户信息服务
- `CarouselService.java`: 轮播图服务
- `TokenService.java`: 令牌处理服务

#### 5.1 service/impl目录

服务层接口的实现类

### 6. utils目录

工具类目录，提供各种辅助功能：

- `JwtUtils.java`: JWT(JSON Web Token)工具类，处理身份验证令牌
- `RedisUtils.java`: Redis操作工具类，封装了Redis常用操作
- `RedisUsageExample.java`: Redis工具类使用示例文档

### 7. filter目录

过滤器目录，包含请求过滤和拦截相关的类

### 8. resources目录

资源文件目录：

- `application.yml`: 主配置文件，设置激活的配置环境
- `application-dev.yml`: 开发环境配置
- `application-dev2.yml`: 备用开发环境配置

## 关键技术说明

1. **Spring Boot**: 简化了Spring应用的初始化和开发过程
2. **Spring Security**: 处理身份验证和授权
3. **MyBatis-Plus**: 增强的MyBatis框架，简化数据库操作
4. **Redis**: 用于缓存和会话管理
5. **JWT**: 用于无状态身份验证
6. **Spring Data Redis**: 简化Redis操作的Spring模块

## 数据流向

1. 客户端发送请求到控制器层
2. 控制器调用服务层处理业务逻辑
3. 服务层通过数据访问层操作数据库
4. 服务层返回结果给控制器
5. 控制器将结果包装成视图对象返回给客户端

## 常见使用场景

1. **用户认证**：通过AuthController处理登录、注册请求
2. **图片上传**：AdmController中的addImg方法处理图片上传
3. **用户信息查询**：通过AccountInfoService获取用户信息，并转换为包含Base64编码图片的DTO

## 工具类使用

项目包含了多个实用的工具类，如：

1. **RedisUtils**: 封装了Redis的常用操作，包括字符串、哈希、列表、集合、有序集合等数据类型的操作。详细使用方法请参考RedisUsageExample.java
2. **JwtUtils**: 用于生成和验证JWT令牌

## 开发指南

1. 添加新功能时，请遵循现有的分层架构
2. 控制器处理请求参数验证和响应封装
3. 服务层处理业务逻辑
4. 数据访问层处理数据库操作
5. 使用DTO在不同层之间传递数据
6. 使用VO封装返回给前端的数据

## 环境配置

项目提供了两套开发环境配置：
- `application-dev.yml`: 主要开发环境
- `application-dev2.yml`: 备用开发环境

在`application.yml`中设置`spring.profiles.active`来选择使用哪个环境配置。 