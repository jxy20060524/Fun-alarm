-- 请在命令行执行（注意 charset 参数）：
-- mysql -u root -p --default-character-set=utf8mb4 < sql/init.sql

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS fun_alarm;
CREATE DATABASE fun_alarm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fun_alarm;

DROP TABLE IF EXISTS answer_attempts;
DROP TABLE IF EXISTS wake_sessions;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS alarms;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    nickname    VARCHAR(50) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alarms (
    alarm_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    alarm_time  TIME NOT NULL,
    repeat_days VARCHAR(7) NOT NULL,
    ringtone    VARCHAR(100) NOT NULL DEFAULT 'default.wav',
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    label       VARCHAR(50),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_alarms_user ON alarms(user_id);

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    content     VARCHAR(500) CHARACTER SET utf8mb4 NOT NULL,
    option_a    VARCHAR(200) CHARACTER SET utf8mb4 NOT NULL,
    option_b    VARCHAR(200) CHARACTER SET utf8mb4 NOT NULL,
    option_c    VARCHAR(200) CHARACTER SET utf8mb4 NOT NULL,
    option_d    VARCHAR(200) CHARACTER SET utf8mb4 NOT NULL,
    answer      CHAR(1) NOT NULL,
    difficulty  TINYINT NOT NULL DEFAULT 1,
    CONSTRAINT chk_answer CHECK (answer IN ('A','B','C','D'))
);

CREATE TABLE wake_sessions (
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

CREATE TABLE answer_attempts (
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
('什么东西越洗越脏？', '衣服', '水', '汽车', '手', 'B', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('为什么蜘蛛上网从来不卡', '网速快', '会修电脑', '因为网是它自己织的', '开会员了', 'C', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('什么水果最容易迷路?', '苹果', '草莓', '芒果', '榴莲', 'D', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('歇后语：猪八戒照镜子————', '越照越帅', '里外不是人', '不敢看自己', '镜子碎了', 'B', 2);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('早起的鸟儿有虫吃的下一句常见说法是?', '晚起的鸟儿也有虫吃', '早起的虫儿被鸟吃', '鸟儿都不早起', '没有下一句', 'B', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('歇后语：老奶奶钻被窝————', '暖和', '给爷整笑了', '睡觉打呼噜', '冻死了', 'B', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('歇后语：小葱拌豆腐————', '一清二白', '很好吃', '清淡可口', '不放盐', 'A', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('有一只狼来到了北极，不小心掉进海里，变成了什么？', '死狼', '海水狼', '槟榔', '白狼', 'C', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('为什么蚕宝宝很有钱？', '会赚钱', '因为会结茧', '不花钱', '家里有矿', 'B', 2);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('歇后语；飞机上吊暖瓶————', '高水平', '很危险', '容易掉', '飞得高', 'A', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('为什么鲁智深最适合当园丁？', '力气大', '喜欢植物', '因为会倒把垂杨柳', '有经验', 'C', 2);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('如果把地球当作一张脸，那非洲就代表鼻子的位置，为啥？', '因为非洲的轮廓像鼻子', '因为非洲在地图中央', '因为黑头多', '因为非洲没有水', 'C', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('日本人去看牙医，为什么跟医生干起来了？', '医生技术不好', '他去错医院了', '心情不好', '因为医生会说：拔个牙咯？', 'D', 2);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('小明看到妈妈倒立了，小明会说什么？', 'WOW', '妈妈好厉害', '小心呀', '妈妈我也要学', 'A', 1);

INSERT INTO questions (content, option_a, option_b, option_c, option_d, answer, difficulty) VALUES
('白雪公主为什么生活不幸福?', '因为吃了毒苹果', '因为没有找到白马王子', '因为身边有小人', '生母早逝', 'C', 1);
