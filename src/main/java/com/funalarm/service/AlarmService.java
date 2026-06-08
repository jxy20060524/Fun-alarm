package com.funalarm.service;

import com.funalarm.dao.AlarmDAO;
import com.funalarm.model.Alarm;
import com.funalarm.util.RingtoneCatalog;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class AlarmService {
    private final AlarmDAO alarmDAO = new AlarmDAO();

    public List<Alarm> listByUser(int userId) throws SQLException {
        return alarmDAO.findByUserId(userId);
    }

    public List<Alarm> listActiveByUser(int userId) throws SQLException {
        return alarmDAO.findActiveByUserId(userId);
    }

    public Alarm create(Alarm alarm) throws SQLException {
        validateAlarm(alarm);
        return alarmDAO.insert(alarm);
    }

    public void update(Alarm alarm) throws SQLException {
        validateAlarm(alarm);
        alarmDAO.update(alarm);
    }

    public void delete(int alarmId) throws SQLException {
        alarmDAO.delete(alarmId);
    }

    public void validateTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("请选择闹钟时间");
        }
    }

    private void validateAlarm(Alarm alarm) {
        validateTime(alarm.getAlarmTime());
        if (alarm.getRepeatDays() == null || !alarm.getRepeatDays().matches("[01]{7}")) {
            throw new IllegalArgumentException("重复周期格式无效");
        }
        if (!alarm.getRepeatDays().contains("1")) {
            throw new IllegalArgumentException("请至少选择一天重复");
        }
        if (alarm.getRingtone() == null || alarm.getRingtone().isBlank()) {
            alarm.setRingtone(RingtoneCatalog.defaultRingtone());
        }
    }
}
