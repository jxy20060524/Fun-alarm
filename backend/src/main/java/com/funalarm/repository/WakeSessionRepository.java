package com.funalarm.repository;

import com.funalarm.entity.WakeSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WakeSessionRepository extends JpaRepository<WakeSession, Integer> {
    boolean existsByAlarmIdAndSessionDate(Integer alarmId, LocalDate sessionDate);
    List<WakeSession> findByUserIdAndSuccessTrueOrderBySessionDateDescAlarmTimeAsc(Integer userId);
    List<WakeSession> findByUserIdOrderByTriggerAtDesc(Integer userId, org.springframework.data.domain.Pageable pageable);
}
