# 趣味闹钟 (Fun Alarm)

答对题目才能关闭闹钟 —— **Spring Boot + Vue 3** 前后端分离，支持手机与电脑浏览器访问，数据存储在 **腾讯云 MySQL**。

## 功能

- 用户注册 / 登录（无需密码）
- 闹钟增删改查、铃声选择
- 到点全屏响铃 + 趣味选择题（连错 3 次换题）
- 激励页手动关闭闹钟
- 起床统计与连续早起天数

## 架构

```
浏览器（手机/电脑） → Vue 前端 → Spring Boot API → 腾讯云 MySQL
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- 腾讯云 CynosDB / MySQL（已执行 `sql/init.sql`）

## 一、配置腾讯云数据库

1. 在腾讯云控制台对 CynosDB 实例 **开启外网地址**（本地开发必须用外网，内网 `172.21.0.2` 仅同 VPC 云服务器可用）
2. 在数据库中执行：

```bash
mysql -h 你的外网地址 -P 3306 -u root -p --default-character-set=utf8mb4 < sql/init.sql
```

3. 配置后端连接（**密码不要写进会提交的文件**）：

**推荐：本地私密文件**

复制 `backend/src/main/resources/application.example.yml` 为 `application-local.yml`，填入密码后启动：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local,default
```

**或使用环境变量：**

```bash
set DB_HOST=bj-cynosdbmysql-grp-7t95af7a.sql.tencentcdb.com
set DB_PORT=20873
set DB_USERNAME=root
set DB_PASSWORD=你的密码
set DB_NAME=fun_alarm
```

> 外网地址：`bj-cynosdbmysql-grp-7t95af7a.sql.tencentcdb.com:20873`  
> 内网地址（仅同 VPC 云服务器）：`172.21.0.2:3306`

## 二、启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认：`http://localhost:8080`

## 三、启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认：`http://localhost:5173`（已代理 `/api` 到后端）

## 四、使用说明

1. 浏览器打开 `http://localhost:5173`（手机可用同 WiFi 访问电脑 IP:5173）
2. 注册 → 登录 → 新建闹钟
3. **保持浏览器页面打开**（可切到后台标签，但不要关闭），到点自动响铃
4. 答对进入激励页 → 点击「关闭闹钟」停止音乐 → 返回首页

## 项目结构

```
Alarm/
├── backend/          Spring Boot 后端
├── frontend/         Vue 3 前端
├── sql/init.sql      数据库初始化
└── README.md
```

## 部署上线（可选）

- 后端：打包 `mvn package` 部署到云服务器，配置环境变量连接腾讯云数据库
- 前端：`npm run build` 后将 `dist/` 部署到 Nginx，或与服务端同域
- 修改 `application.yml` 中 `app.cors.allowed-origins` 为生产域名

## 技术栈

Spring Boot 3 · Spring Data JPA · Vue 3 · Vite · Pinia · 腾讯云 MySQL
