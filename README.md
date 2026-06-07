# 趣味闹钟 (Fun Alarm)

答对题目才能关闭闹钟的 Java 桌面程序，适用于软件设计课程演示。

## 功能

- 用户注册 / 登录（无需密码）
- 闹钟增删改查（时间限制 05:00–09:00）
- 到点全屏响铃 + 趣味选择题
- 连错 3 次公布答案并换题
- 起床统计与连续早起天数
- 系统托盘后台运行

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x

## 快速开始

### 1. 克隆项目

```bash
git clone <仓库地址>
cd Alarm
```

### 2. 初始化数据库

在 MySQL 中执行：

```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置数据库连接

编辑项目根目录或 `src/main/resources/config.properties`：

```properties
db.url=jdbc:mysql://localhost:3306/fun_alarm?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
db.username=root
db.password=你的密码
```

### 4. 运行

**推荐：IntelliJ 运行配置**

1. 在 IntelliJ 右上角选择 **FunAlarmApp** 或 **FunAlarm (Maven)** 运行配置
2. 首次运行前，右键 `pom.xml` → **Maven** → **Reload Project**，等待依赖下载完成
3. 点击运行

若直接运行 `FunAlarmApp` 出现「缺少 JavaFX 运行时组件」，请改用 **FunAlarmApp**（Launcher 入口）或 **FunAlarm (Maven)** 配置。

**方式 A：Maven（命令行）**

```bash
mvn clean javafx:run
```

**方式 B：IDE 手动 VM 参数**

主类设为 `com.funalarm.Launcher`，添加 VM options：

```
--module-path "target/javafx-modules" --add-modules javafx.controls,javafx.fxml,javafx.media
```

运行前先执行 `mvn process-resources` 生成 `target/javafx-modules` 目录。

### 5. 演示流程

1. 注册用户名 → 登录
2. 新建闹钟（建议设为当前时间后 1–2 分钟）
3. 保持程序运行（可最小化到托盘）
4. 到点后全屏响铃，答对题目关闭
5. 查看统计页与激励页

## 项目结构

```
src/main/java/com/funalarm/
├── FunAlarmApp.java          # 入口
├── config/                   # 数据库配置
├── model/                    # 实体类
├── dao/                      # 数据访问
├── service/                  # 业务逻辑
├── scheduler/                # 闹钟调度
├── ui/                       # JavaFX 界面
└── util/                     # 工具类
sql/init.sql                  # 建表与题库初始化
config.properties             # 数据库连接（运行目录优先）
```

## 注意事项

- 本程序为**桌面模拟闹钟**，需保持进程运行；关闭窗口会最小化到托盘（若系统支持）。
- 同一闹钟同一天只自动触发一次。
- 早起判定：在设定时间起 20 分钟内答对即算早起成功。
- 若无铃声文件，将使用系统 beep 作为备用提示音。

## 技术栈

Java 17 · JavaFX · MySQL · JDBC · HikariCP · Maven
