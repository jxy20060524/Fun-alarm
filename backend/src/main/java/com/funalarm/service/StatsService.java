package com.funalarm.service;

import com.funalarm.config.AppConstants;
import com.funalarm.entity.WakeSession;
import com.funalarm.repository.WakeSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class StatsService {
    private final WakeSessionRepository wakeSessionRepository;

    public StatsService(WakeSessionRepository wakeSessionRepository) {
        this.wakeSessionRepository = wakeSessionRepository;
    }

    public Optional<LocalDateTime> getTodayWakeTime(Integer userId) {
        LocalDate today = LocalDate.now();
        return wakeSessionRepository.findByUserIdAndSuccessTrueOrderBySessionDateDescAlarmTimeAsc(userId).stream()
                .filter(s -> s.getSessionDate().equals(today))
                .min(Comparator.comparing(WakeSession::getAlarmTime))
                .map(WakeSession::getSuccessAt);
    }

    public int getStreakDays(Integer userId) {
        Set<LocalDate> validDays = new HashSet<>();
        for (WakeSession session : wakeSessionRepository.findByUserIdAndSuccessTrueOrderBySessionDateDescAlarmTimeAsc(userId)) {
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

    public List<WakeSession> getRecentSessions(Integer userId, int limit) {
        return wakeSessionRepository.findByUserIdOrderByTriggerAtDesc(userId, PageRequest.of(0, limit));
    }

    public boolean isEarlyWakeSuccess(WakeSession session) {
        if (!Boolean.TRUE.equals(session.getSuccess()) || session.getSuccessAt() == null) {
            return false;
        }
        LocalDateTime deadline = LocalDateTime.of(session.getSessionDate(), session.getAlarmTime())
                .plusMinutes(AppConstants.EARLY_WAKE_MINUTES);
        return !session.getSuccessAt().isAfter(deadline);
    }

    public String formatRepeatDays(String repeatDays) {
        if (repeatDays == null || repeatDays.length() != 7) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (repeatDays.charAt(i) == '1') {
                if (!sb.isEmpty()) sb.append(" ");
                sb.append(AppConstants.DAY_LABELS[i]);
            }
        }
        return sb.toString();
    }

    public String formatTime(LocalTime time) {
        return time == null ? "--:--" : String.format("%02d:%02d", time.getHour(), time.getMinute());
    }

    public String randomMotivation() {
        return AppConstants.MOTIVATION_MESSAGES[new Random().nextInt(AppConstants.MOTIVATION_MESSAGES.length)];
    }
}
