package com.funalarm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "alarms")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer alarmId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "alarm_time", nullable = false)
    private LocalTime alarmTime;

    @Column(name = "repeat_days", nullable = false, length = 7)
    private String repeatDays;

    @Column(nullable = false, length = 100)
    private String ringtone = "happy.mp3";

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(length = 50)
    private String label;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getAlarmId() { return alarmId; }
    public void setAlarmId(Integer alarmId) { this.alarmId = alarmId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalTime alarmTime) { this.alarmTime = alarmTime; }
    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }
    public String getRingtone() { return ringtone; }
    public void setRingtone(String ringtone) { this.ringtone = ringtone; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
