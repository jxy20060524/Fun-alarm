package com.funalarm.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Alarm {
    private int alarmId;
    private int userId;
    private LocalTime alarmTime;
    private String repeatDays;
    private String ringtone;
    private boolean active;
    private String label;
    private LocalDateTime createdAt;

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRepeatOnDay(int dayOfWeek) {
        if (repeatDays == null || repeatDays.length() != 7) {
            return false;
        }
        int index = dayOfWeek - 1;
        return repeatDays.charAt(index) == '1';
    }
}
