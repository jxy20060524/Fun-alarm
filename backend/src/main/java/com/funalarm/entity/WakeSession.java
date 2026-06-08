package com.funalarm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "wake_sessions")
public class WakeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "alarm_id", nullable = false)
    private Integer alarmId;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "alarm_time", nullable = false)
    private LocalTime alarmTime;

    @Column(name = "trigger_at", nullable = false)
    private LocalDateTime triggerAt;

    @Column(name = "success_at")
    private LocalDateTime successAt;

    @Column(name = "is_success", nullable = false)
    private Boolean success = false;

    @Column(name = "wrong_count", nullable = false)
    private Integer wrongCount = 0;

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getAlarmId() { return alarmId; }
    public void setAlarmId(Integer alarmId) { this.alarmId = alarmId; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public LocalTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalTime alarmTime) { this.alarmTime = alarmTime; }
    public LocalDateTime getTriggerAt() { return triggerAt; }
    public void setTriggerAt(LocalDateTime triggerAt) { this.triggerAt = triggerAt; }
    public LocalDateTime getSuccessAt() { return successAt; }
    public void setSuccessAt(LocalDateTime successAt) { this.successAt = successAt; }
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public Integer getWrongCount() { return wrongCount; }
    public void setWrongCount(Integer wrongCount) { this.wrongCount = wrongCount; }
}
