-- 已有数据库需执行本脚本，移除闹钟 5:00-9:00 限制（新安装可忽略）
USE fun_alarm;
ALTER TABLE alarms DROP CHECK chk_alarm_time;
