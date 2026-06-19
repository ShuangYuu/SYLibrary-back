# 数字图书元数据采集系统 - 后端服务

本项目是“数字图书元数据采集系统”的 Spring Boot 后端，提供管理员管理、用户管理、图书元数据维护、外部书籍导入、用户收藏、头像上传、工作台统计等 REST API。

## 技术栈

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Security
- JWT
- MyBatis
- PageHelper
- MySQL
- Redis
- Lombok
- Hutool
- Maven Wrapper

## 功能模块

- 管理员认证：管理员密码登录、JWT 访问令牌、刷新令牌、退出登录。
- 用户认证：用户密码登录、JWT 刷新、退出登录。
- 权限控制：基于 Spring Security 和 JWT 区分 ADMIN / USER 权限。
- 管理员管理：分页查询、新增、编辑、删除、当前管理员信息。
- 用户管理：分页查询、新增、编辑、删除、当前用户信息。
- 用户头像：本地头像上传和静态资源访问。
- 用户收藏：收藏列表、判断收藏状态、添加收藏、取消收藏。
- 图书管理：分页查询、新增、编辑、删除、首页图书数据。
- 外部书籍导入：支持 Open Library 和 Google Books 搜索、过滤、去重、批量导入。
- 工作台统计：近 10 天新增图书数量、近 10 天用户总量趋势。

## 目录结构

```text
SpringBoot/
├─ src/
│  ├─ main/
│  │  ├─ java/org/springboot/
│  │  │  ├─ config/          # 安全、上传、外部平台配置
│  │  │  ├─ controller/      # REST 控制器
│  │  │  ├─ entity/          # 实体、DTO、请求对象
│  │  │  ├─ exception/       # 业务异常和全局异常处理
│  │  │  ├─ mapper/          # MyBatis Mapper
│  │  │  ├─ service/         # 服务接口、外部平台客户端
│  │  │  ├─ service/impl/    # 服务实现
│  │  │  ├─ utils/           # JWT、密码、Redis 常量等工具
│  │  │  └─ Application.java
│  │  └─ resources/
│  │     ├─ application.yml
│  │     ├─ application-dev.yml
│  │     ├─ application-prod.yml
│  │     └─ sql/
│  │        └─ database.sql  # 数据库初始化脚本
│  └─ test/
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
└─ README.md
```

## 环境要求

- JDK 17+
- Maven Wrapper 已随项目提交，无需单独安装 Maven
- MySQL 8 或兼容版本
- Redis

## 必要环境变量

运行前至少需要设置：

```text
JWT_SECRET=足够长的 JWT 签名密钥
```

生产环境还需要：

```text
MYSQL_PASSWORD=数据库密码
REDIS_PASSWORD=Redis 密码
PUBLIC_BASE_URL=后端公开访问地址，例如 http://localhost:8080
```

Google Books 可选：

```text
GOOGLE_BOOK_API_KEY=Google Books API Key
```

## 配置文件

默认配置：

```text
src/main/resources/application.yml
```

开发环境配置：

```text
src/main/resources/application-dev.yml
```

生产环境配置：

```text
src/main/resources/application-prod.yml
```

当前默认 profile 为 `dev`。开发环境默认连接：

- MySQL：`localhost:3306/sylibrary`
- Redis：`127.0.0.1:6379`
- 后端端口：`8080`

头像上传路径由 `app.upload` 配置控制。

## 数据库初始化

在 MySQL 中执行后端资源目录下的初始化脚本：

```sql
src/main/resources/sql/database.sql
```

该脚本会创建 `sylibrary` 数据库和主要表：

- `user`
- `admin`
- `refreshToken`
- `book`
- `user_favorite_book`

后续如果调整表结构，也建议继续维护 `src/main/resources/sql/database.sql`，或在该目录下补充清晰命名的增量脚本。

## 启动服务

Windows：

```sh
.\mvnw.cmd spring-boot:run
```

Linux / macOS：

```sh
./mvnw spring-boot:run
```

服务默认启动在：

```text
http://localhost:8080
```

## 运行测试

Windows：

```sh
.\mvnw.cmd test
```

Linux / macOS：

```sh
./mvnw test
```

## 打包

Windows：

```sh
.\mvnw.cmd clean package
```

Linux / macOS：

```sh
./mvnw clean package
```

## 主要接口

### 管理员

- `GET /admin/`：查询全部管理员
- `GET /admin/page`：分页查询管理员
- `GET /admin/info`：当前管理员信息
- `POST /admin/`：新增管理员
- `PUT /admin/`：编辑管理员
- `DELETE /admin/{id}`：删除管理员
- `POST /admin/login/password`：管理员密码登录
- `POST /admin/refresh`：刷新管理员 Token
- `POST /admin/deleteToken`：管理员退出登录

### 用户

- `GET /user/`：查询全部用户
- `GET /user/page`：分页查询用户
- `GET /user/info`：当前用户信息
- `POST /user/`：新增用户
- `PUT /user/`：编辑用户
- `DELETE /user/{id}`：删除用户
- `POST /user/avatar`：上传用户头像
- `GET /user/favorites`：查询当前用户收藏
- `GET /user/favorites/{bookId}`：判断图书是否已收藏
- `POST /user/favorites/{bookId}`：收藏图书
- `DELETE /user/favorites/{bookId}`：取消收藏
- `POST /user/login/password`：用户密码登录
- `POST /user/refresh`：刷新用户 Token
- `POST /user/deleteToken`：用户退出登录

### 图书

- `GET /book/page`：分页查询图书
- `POST /book/`：新增图书
- `PUT /book/`：编辑图书
- `DELETE /book/{bookId}`：删除图书
- `GET /book/home`：客户端首页图书数据
- `GET /book/external/search`：搜索外部书籍
- `POST /book/import/selected`：导入选中外部书籍
- `POST /book/import/open-library`：按条件导入 Open Library 书籍

### 工作台

- `GET /dashboard/trends`：近 10 天新增图书数量和用户总量趋势

## 权限说明

安全规则位于：

```text
src/main/java/org/springboot/config/SecurityConfig.java
```

大致规则：

- 登录、刷新、部分公共图书和头像资源允许匿名访问。
- `/dashboard/**`、外部图书导入、管理员管理等接口需要 ADMIN。
- 普通图书浏览、用户个人信息、收藏等接口需要 USER 或 ADMIN。

请求受保护接口时需要携带：

```text
Authorization: Bearer <accessToken>
```

## 外部书籍导入

支持来源：

- Open Library
- Google Books

配置项位于：

```text
book-import.open-library
book-import.google-books
```

开发环境可通过代理访问外部平台；生产环境默认关闭代理。Google Books API Key 通过 `GOOGLE_BOOK_API_KEY` 注入。

## 头像上传

头像上传接口：

```text
POST /user/avatar
```

请求类型为 `multipart/form-data`，字段名为：

```text
file
```

静态资源映射由 `WebMvcConfig` 和 `app.upload` 控制。

## 开发注意事项

- 数据库字段使用 `createdTime` / `updateTime` 命名，新增 SQL 和 Mapper 时注意保持一致。
- `JWT_SECRET` 未设置时，JWT 工具会拒绝生成或校验令牌。
- 不要把生产数据库密码、Redis 密码、JWT 密钥提交到仓库。
- 新增接口时同步检查 `SecurityConfig` 的权限规则。
- 修改数据库结构时同步维护 `src/main/resources/sql/database.sql` 或同目录下的增量脚本。
