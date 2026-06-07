package com.funalarm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WakeSession {
    private int sessionId;
    private int userId;
    private int alarmId;
    private LocalDate sessionDate;
    private LocalTime alarmTime;
    private LocalDateTime triggerAt;
    private LocalDateTime successAt;
    private boolean success;
    private int wrongCount;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public LocalDateTime getTriggerAt() {
        return triggerAt;
    }

    public void setTriggerAt(LocalDateTime triggerAt) {
        this.triggerAt = triggerAt;
    }

    public LocalDateTime getSuccessAt() {
        return successAt;
    }

    public void setSuccessAt(LocalDateTime successAt) {
        this.successAt = successAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }
}
