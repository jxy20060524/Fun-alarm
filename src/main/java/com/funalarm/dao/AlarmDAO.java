package com.funalarm.dao;

import com.funalarm.config.DatabaseConfig;
import com.funalarm.model.Alarm;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlarmDAO {

    public List<Alarm> findByUserId(int userId) throws SQLException {
        String sql = """
                SELECT alarm_id, user_id, alarm_time, repeat_days, ringtone, is_active, label, created_at
                FROM alarms WHERE user_id = ? ORDER BY alarm_time
                """;
        List<Alarm> alarms = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alarms.add(mapRow(rs));
                }
            }
        }
        return alarms;
    }

    public List<Alarm> findActiveByUserId(int userId) throws SQLException {
        String sql = """
                SELECT alarm_id, user_id, alarm_time, repeat_days, ringtone, is_active, label, created_at
                FROM alarms WHERE user_id = ? AND is_active = 1 ORDER BY alarm_time
                """;
        List<Alarm> alarms = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alarms.add(mapRow(rs));
                }
            }
        }
        return alarms;
    }

    public Optional<Alarm> findById(int alarmId) throws SQLException {
        String sql = """
                SELECT alarm_id, user_id, alarm_time, repeat_days, ringtone, is_active, label, created_at
                FROM alarms WHERE alarm_id = ?
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alarmId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Alarm insert(Alarm alarm) throws SQLException {
        String sql = """
                INSERT INTO alarms (user_id, alarm_time, repeat_days, ringtone, is_active, label)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindAlarm(ps, alarm);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    alarm.setAlarmId(keys.getInt(1));
                    return alarm;
                }
            }
        }
        throw new SQLException("创建闹钟失败");
    }

    public void update(Alarm alarm) throws SQLException {
        String sql = """
                UPDATE alarms SET alarm_time = ?, repeat_days = ?, ringtone = ?, is_active = ?, label = ?
                WHERE alarm_id = ?
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTime(1, Time.valueOf(alarm.getAlarmTime()));
            ps.setString(2, alarm.getRepeatDays());
            ps.setString(3, alarm.getRingtone());
            ps.setBoolean(4, alarm.isActive());
            ps.setString(5, alarm.getLabel());
            ps.setInt(6, alarm.getAlarmId());
            ps.executeUpdate();
        }
    }

    public void delete(int alarmId) throws SQLException {
        String sql = "DELETE FROM alarms WHERE alarm_id = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alarmId);
            ps.executeUpdate();
        }
    }

    private void bindAlarm(PreparedStatement ps, Alarm alarm) throws SQLException {
        ps.setInt(1, alarm.getUserId());
        ps.setTime(2, Time.valueOf(alarm.getAlarmTime()));
        ps.setString(3, alarm.getRepeatDays());
        ps.setString(4, alarm.getRingtone());
        ps.setBoolean(5, alarm.isActive());
        ps.setString(6, alarm.getLabel());
    }

    private Alarm mapRow(ResultSet rs) throws SQLException {
        Alarm alarm = new Alarm();
        alarm.setAlarmId(rs.getInt("alarm_id"));
        alarm.setUserId(rs.getInt("user_id"));
        alarm.setAlarmTime(rs.getTime("alarm_time").toLocalTime());
        alarm.setRepeatDays(rs.getString("repeat_days"));
        alarm.setRingtone(rs.getString("ringtone"));
        alarm.setActive(rs.getBoolean("is_active"));
        alarm.setLabel(rs.getString("label"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            alarm.setCreatedAt(ts.toLocalDateTime());
        }
        return alarm;
    }
}
