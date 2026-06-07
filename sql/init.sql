CREATE DATABASE IF NOT EXISTS fun_alarm DEFAULT CHARSET utf8mb4;
USE fun_alarm;

CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    nickname    VARCHAR(50) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alarms (
    alarm_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    alarm_time  TIME NOT NULL,
    repeat_days VARCHAR(7) NOT NULL,
    ringtone    VARCHAR(100) NOT NULL DEFAULT 'default.wav',
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    label       VARCHAR(50),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_alarm_time CHECK (alarm_time >= '05:00:00' AND alarm_time <= '09:00:00')
);

CREATE INDEX idx_alarms_user ON alarms(user_id);

CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT NOT NULL,
    option_a    VARCHAR(200) NOT NULL,
    option_b    VARCHAR(200) NOT NULL,
    option_c    VARCHAR(200) NOT NULL,
    option_d    VARCHAR(200) NOT NULL,
    answer      CHAR(1) NOT NULL,
    difficulty  TINYINT NOT NULL DEFAULT 1,
    CONSTRAINT chk_answer CHECK (answer IN ('A','B','C','D'))
);

CREATE TABLE IF NOT EXISTS wake_sessions (
    session_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    alarm_id     INT NOT NULL,
    session_date DATE NOT NULL,
    alarm_time   TIME NOT NULL,
    trigger_at   DATETIME NOT NULL,
    success_at   DATETIME NULL,
    is_success   TINYINT(1) NOT NULL DEFAULT 0,
    wrong_count  INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (alarm_id) REFERENCES alarms(alarm_id) ON DELETE CASCADE
);

CREATE INDEX idx_wake_user_date ON wake_sessions(user_id, session_date);

CREATE TABLE IF NOT EXISTS answer_attempts (
    attempt_id   INT AUTO_INCREMENT PRIMARY KEY,
    session_id   INT NOT NULL,
    question_id  INT NOT NULL,
    user_answer  CHAR(1) NOT NULL,
    is_correct   TINYINT(1) NOT NULL,
    attempted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES wake_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('2 + 3 × 4 等于多少？', '20', '14', '24', '11', 'B', 1),
('北京交通大学位于哪个城市？', '上海', '北京', '天津', '石家庄', 'B', 1),
('一年有多少个月？', '10', '11', '12', '13', 'C', 1),
('下列哪个是 Java 的基本数据类型？', 'String', 'Integer', 'int', 'Object', 'C', 2),
('「早起的鸟儿有虫吃」的下一句常见说法是？', '晚起的鸟儿也有虫吃', '早起的虫儿被鸟吃', '鸟儿都不早起', '没有下一句', 'B', 1),
('一周有几天？', '5', '6', '7', '8', 'C', 1),
('100 除以 4 等于多少？', '20', '25', '30', '40', 'B', 1),
('太阳从哪个方向升起？', '西方', '南方', '北方', '东方', 'D', 1),
('下列哪个不是编程语言？', 'Java', 'Python', 'HTML', 'C++', 'C', 2),
('3 的平方是多少？', '6', '9', '12', '27', 'B', 1),
('中国最长的河流是？', '黄河', '长江', '珠江', '淮河', 'B', 2),
('1 公里等于多少米？', '100', '500', '1000', '10000', 'C', 1),
('下列哪个是质数？', '4', '6', '9', '7', 'D', 2),
('一天有多少小时？', '12', '20', '24', '48', 'C', 1),
('「软件工程」课程通常属于哪个学院？', '土木', '计算机', '机械', '经管', 'B', 1);
