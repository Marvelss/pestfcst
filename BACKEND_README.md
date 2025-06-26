# 病虫害预测系统 - 后端服务

## 项目概述

这是一个基于Spring Boot的微服务后端系统，提供用户管理、文件上传下载等核心功能。

## 技术栈

- **框架**: Spring Boot 2.7+
- **数据库**: PostgreSQL
- **ORM**: Spring Data JPA
- **消息队列**: Kafka (可选)
- **服务发现**: Nacos (可选)
- **构建工具**: Maven

## 项目结构

```
model-building-service/
├── src/main/java/cn/edu/hdu/pestfcst/modelbuildingservice/
│   ├── bean/                    # 实体类
│   │   ├── User.java           # 用户实体
│   │   ├── FileRecord.java     # 文件记录实体
│   │   └── ApiResponse.java    # API响应类
│   ├── controller/             # 控制器
│   │   ├── UserController.java # 用户管理控制器
│   │   ├── ApiDocController.java # API文档控制器
│   │   └── TestController.java # 测试控制器
│   ├── service/                # 服务层
│   │   ├── FileService.java    # 文件服务接口
│   │   └── impl/
│   │       └── FileServiceImpl.java # 文件服务实现
│   ├── dao/                    # 数据访问层
│   │   ├── UserRepository.java # 用户Repository
│   │   └── FileRecordRepository.java # 文件Repository
│   ├── config/                 # 配置类
│   │   └── FileUploadConfig.java # 文件上传配置
│   ├── exception/              # 异常处理
│   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   └── ModelBuildingServiceApplication.java # 启动类
└── src/main/resources/
    └── application.yml         # 配置文件
```

## 环境要求

- Java 8+
- Maven 3.6+
- PostgreSQL 12+
- Kafka 2.8+ (可选)
- Nacos 2.0+ (可选)

## 快速开始

### 1. 数据库准备

#### 安装PostgreSQL
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# CentOS/RHEL
sudo yum install postgresql postgresql-server

# Windows
# 下载并安装: https://www.postgresql.org/download/windows/
```

#### 创建数据库
```sql
-- 登录PostgreSQL
psql -U postgres

-- 创建数据库
CREATE DATABASE pest_model_building;

-- 创建用户（可选）
CREATE USER pest_user WITH PASSWORD 'pest_password';
GRANT ALL PRIVILEGES ON DATABASE pest_model_building TO pest_user;

-- 退出
\q
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pest_model_building?useSSL=false&serverTimezone=UTC
    username: postgres  # 或你的用户名
    password: 123456    # 或你的密码
```

### 3. 编译和运行

```bash
# 进入项目目录
cd pestfcst/model-building-service

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

服务将在 `http://localhost:8092` 启动

### 4. 验证服务

访问以下URL验证服务是否正常：

- 健康检查: `http://localhost:8092/api/health`
- API文档: `http://localhost:8092/api/docs`
- 测试接口: `http://localhost:8092/test/hello`
- 数据库测试: `http://localhost:8092/test/db-test`

## API接口文档

### 用户管理接口

#### 1. 用户注册
```http
POST /api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 2. 用户登录
```http
POST /api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

#### 3. 修改密码
```http
POST /api/user/change-password
Content-Type: application/json

{
  "userId": 1,
  "oldPassword": "123456",
  "newPassword": "newpassword"
}
```

#### 4. 获取用户信息
```http
GET /api/user/info/{userId}
```

#### 5. 检查用户名
```http
GET /api/user/check-username/{username}
```

### 文件管理接口

#### 1. 上传单个文件
```http
POST /api/user/upload
Content-Type: multipart/form-data

file: [文件]
userId: 1
description: 文件描述（可选）
```

#### 2. 批量上传文件
```http
POST /api/user/upload-multiple
Content-Type: multipart/form-data

files: [文件数组]
userId: 1
```

#### 3. 下载文件
```http
GET /api/user/download/{fileId}?userId={userId}
```

#### 4. 删除文件
```http
DELETE /api/user/delete-file/{fileId}?userId={userId}
```

#### 5. 获取文件列表
```http
GET /api/user/files/{userId}?status=active
```

#### 6. 获取文件统计
```http
GET /api/user/files/{userId}/stats
```

## 功能特性

### 用户管理
- ✅ 用户注册和登录
- ✅ 密码修改
- ✅ 用户信息查询
- ✅ 用户名可用性检查
- ✅ 参数验证和错误处理

### 文件管理
- ✅ 单文件上传
- ✅ 批量文件上传
- ✅ 文件下载
- ✅ 文件删除（软删除）
- ✅ 文件列表查询
- ✅ 文件统计信息
- ✅ 文件类型验证
- ✅ 文件大小限制
- ✅ 用户权限验证

### 系统特性
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ 跨域支持
- ✅ 文件上传配置
- ✅ 数据库连接池
- ✅ 日志记录
- ✅ 健康检查
- ✅ API文档

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pest_model_building
    username: postgres
    password: 123456
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

file:
  upload:
    path: uploads
    max-size: 10485760
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,txt,csv,zip,rar
```

### 日志配置
```yaml
logging:
  level:
    cn.edu.hdu.pestfcst: debug
  file:
    name: logs/model-building-service.log
    max-size: 10MB
```

## 测试

### 使用curl测试API

#### 1. 创建测试用户
```bash
curl -X POST http://localhost:8092/test/create-test-user
```

#### 2. 用户注册
```bash
curl -X POST http://localhost:8092/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

#### 3. 用户登录
```bash
curl -X POST http://localhost:8092/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

#### 4. 上传文件
```bash
curl -X POST http://localhost:8092/api/user/upload \
  -F "file=@/path/to/your/file.txt" \
  -F "userId=1" \
  -F "description=测试文件"
```

#### 5. 获取文件列表
```bash
curl -X GET "http://localhost:8092/api/user/files/1"
```

## 部署

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package

# 运行JAR文件
java -jar target/model-building-service-0.0.1-SNAPSHOT.jar
```

### Docker部署（可选）
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/model-building-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 常见问题

### 1. 数据库连接失败
- 检查PostgreSQL服务是否启动
- 验证数据库连接配置
- 确认数据库用户名和密码正确

### 2. 文件上传失败
- 检查上传目录权限
- 确认文件大小不超过限制
- 验证用户ID是否正确

### 3. 端口被占用
```bash
# 查看端口占用
netstat -tulpn | grep 8092

# 杀死占用进程
kill -9 <PID>
```

### 4. 内存不足
```bash
# 增加JVM内存
java -Xmx2g -Xms1g -jar target/model-building-service-0.0.1-SNAPSHOT.jar
```

## 监控和日志

### 健康检查
- 端点: `http://localhost:8092/actuator/health`
- 包含数据库连接状态

### 日志文件
- 位置: `logs/model-building-service.log`
- 自动轮转，最大10MB

### 指标监控
- 端点: `http://localhost:8092/actuator/metrics`
- 支持Prometheus格式

## 扩展功能

### 计划中的功能
- [ ] JWT认证
- [ ] 文件预览
- [ ] 文件分享
- [ ] 文件版本管理
- [ ] 云存储集成
- [ ] 异步文件处理
- [ ] 文件压缩

### 安全改进
- [ ] 密码加密存储
- [ ] 文件访问权限控制
- [ ] API限流
- [ ] 输入验证增强

## 联系方式

如有问题或建议，请联系开发团队。

---

**注意**: 本项目仅供学习和演示使用，生产环境请根据实际需求进行安全加固和性能优化。 