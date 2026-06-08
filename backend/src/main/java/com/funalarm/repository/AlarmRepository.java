package com.funalarm.repository;

import com.funalarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    List<Alarm> findByUserIdOrderByAlarmTimeAsc(Integer userId);
    List<Alarm> findByUserIdAndActiveTrueOrderByAlarmTimeAsc(Integer userId);
}
