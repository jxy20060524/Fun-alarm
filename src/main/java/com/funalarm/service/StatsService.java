package com.funalarm.service;

import com.funalarm.dao.WakeDAO;
import com.funalarm.model.WakeSession;
import com.funalarm.util.AppConstants;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class StatsService {
    private final WakeDAO wakeDAO = new WakeDAO();

    public Optional<LocalDateTime> getTodayWakeTime(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        return wakeDAO.findSuccessfulByUser(userId).stream()
                .filter(s -> s.getSessionDate().equals(today))
                .min(Comparator.comparing(WakeSession::getAlarmTime))
                .map(WakeSession::getSuccessAt);
    }

    public int getStreakDays(int userId) throws SQLException {
        Set<LocalDate> validDays = new HashSet<>();
        for (WakeSession session : wakeDAO.findSuccessfulByUser(userId)) {
            if (isEarlyWakeSuccess(session)) {
                validDays.add(session.getSessionDate());
            }
        }

        if (!validDays.contains(LocalDate.now())) {
            return 0;
        }

        int streak = 0;
        LocalDate date = LocalDate.now();
        while (validDays.contains(date)) {
            streak++;
            date = date.minusDays(1);
        }
        return streak;
    }

    public List<WakeSession> getRecentSessions(int userId, int limit) throws SQLException {
        return wakeDAO.findRecentByUser(userId, limit);
    }

    public boolean isEarlyWakeSuccess(WakeSession session) {
        if (!session.isSuccess() || session.getSuccessAt() == null) {
            return false;
        }
        LocalDateTime deadline = LocalDateTime.of(
                session.getSessionDate(),
                session.getAlarmTime()
        ).plusMinutes(AppConstants.EARLY_WAKE_MINUTES);
        return !session.getSuccessAt().isAfter(deadline);
    }

    public String formatRepeatDays(String repeatDays) {
        if (repeatDays == null || repeatDays.length() != 7) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (repeatDays.charAt(i) == '1') {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(AppConstants.DAY_LABELS[i]);
            }
        }
        return sb.toString();
    }

    public String formatTime(LocalTime time) {
        return time == null ? "--:--" : String.format("%02d:%02d", time.getHour(), time.getMinute());
    }
}
