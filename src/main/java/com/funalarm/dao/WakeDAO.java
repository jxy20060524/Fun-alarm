package com.funalarm.dao;

import com.funalarm.config.DatabaseConfig;
import com.funalarm.model.AnswerAttempt;
import com.funalarm.model.WakeSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WakeDAO {

    public WakeSession insertSession(WakeSession session) throws SQLException {
        String sql = """
                INSERT INTO wake_sessions (user_id, alarm_id, session_date, alarm_time, trigger_at, is_success, wrong_count)
                VALUES (?, ?, ?, ?, ?, 0, 0)
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, session.getUserId());
            ps.setInt(2, session.getAlarmId());
            ps.setDate(3, Date.valueOf(session.getSessionDate()));
            ps.setTime(4, Time.valueOf(session.getAlarmTime()));
            ps.setTimestamp(5, Timestamp.valueOf(session.getTriggerAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    session.setSessionId(keys.getInt(1));
                    return session;
                }
            }
        }
        throw new SQLException("创建起床会话失败");
    }

    public Optional<WakeSession> findById(int sessionId) throws SQLException {
        String sql = """
                SELECT session_id, user_id, alarm_id, session_date, alarm_time, trigger_at, success_at, is_success, wrong_count
                FROM wake_sessions WHERE session_id = ?
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapSession(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean hasTriggeredToday(int alarmId, LocalDate date) throws SQLException {
        String sql = "SELECT 1 FROM wake_sessions WHERE alarm_id = ? AND session_date = ? LIMIT 1";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alarmId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateWrongCount(int sessionId, int wrongCount) throws SQLException {
        String sql = "UPDATE wake_sessions SET wrong_count = ? WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wrongCount);
            ps.setInt(2, sessionId);
            ps.executeUpdate();
        }
    }

    public void markSuccess(int sessionId, LocalDateTime successAt) throws SQLException {
        String sql = "UPDATE wake_sessions SET is_success = 1, success_at = ? WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(successAt));
            ps.setInt(2, sessionId);
            ps.executeUpdate();
        }
    }

    public void insertAttempt(AnswerAttempt attempt) throws SQLException {
        String sql = """
                INSERT INTO answer_attempts (session_id, question_id, user_answer, is_correct)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attempt.getSessionId());
            ps.setInt(2, attempt.getQuestionId());
            ps.setString(3, String.valueOf(attempt.getUserAnswer()));
            ps.setBoolean(4, attempt.isCorrect());
            ps.executeUpdate();
        }
    }

    public List<WakeSession> findSuccessfulByUser(int userId) throws SQLException {
        String sql = """
                SELECT session_id, user_id, alarm_id, session_date, alarm_time, trigger_at, success_at, is_success, wrong_count
                FROM wake_sessions WHERE user_id = ? AND is_success = 1 ORDER BY session_date DESC, alarm_time ASC
                """;
        List<WakeSession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapSession(rs));
                }
            }
        }
        return sessions;
    }

    public List<WakeSession> findRecentByUser(int userId, int limit) throws SQLException {
        String sql = """
                SELECT session_id, user_id, alarm_id, session_date, alarm_time, trigger_at, success_at, is_success, wrong_count
                FROM wake_sessions WHERE user_id = ? ORDER BY trigger_at DESC LIMIT ?
                """;
        List<WakeSession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapSession(rs));
                }
            }
        }
        return sessions;
    }

    private WakeSession mapSession(ResultSet rs) throws SQLException {
        WakeSession session = new WakeSession();
        session.setSessionId(rs.getInt("session_id"));
        session.setUserId(rs.getInt("user_id"));
        session.setAlarmId(rs.getInt("alarm_id"));
        session.setSessionDate(rs.getDate("session_date").toLocalDate());
        session.setAlarmTime(rs.getTime("alarm_time").toLocalTime());
        Timestamp trigger = rs.getTimestamp("trigger_at");
        if (trigger != null) {
            session.setTriggerAt(trigger.toLocalDateTime());
        }
        Timestamp success = rs.getTimestamp("success_at");
        if (success != null) {
            session.setSuccessAt(success.toLocalDateTime());
        }
        session.setSuccess(rs.getBoolean("is_success"));
        session.setWrongCount(rs.getInt("wrong_count"));
        return session;
    }
}
