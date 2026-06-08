package com.funalarm.service;

import com.funalarm.config.AppConstants;
import com.funalarm.dto.AlarmRequest;
import com.funalarm.entity.Alarm;
import com.funalarm.repository.AlarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public AlarmService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public List<Alarm> listByUser(Integer userId) {
        return alarmRepository.findByUserIdOrderByAlarmTimeAsc(userId);
    }

    public List<Alarm> listActiveByUser(Integer userId) {
        return alarmRepository.findByUserIdAndActiveTrueOrderByAlarmTimeAsc(userId);
    }

    @Transactional
    public Alarm create(AlarmRequest request) {
        validate(request);
        Alarm alarm = mapToEntity(new Alarm(), request);
        return alarmRepository.save(alarm);
    }

    @Transactional
    public Alarm update(Integer alarmId, AlarmRequest request) {
        validate(request);
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("闹钟不存在"));
        if (!alarm.getUserId().equals(request.userId())) {
            throw new IllegalArgumentException("无权修改此闹钟");
        }
        return alarmRepository.save(mapToEntity(alarm, request));
    }

    @Transactional
    public void delete(Integer alarmId, Integer userId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("闹钟不存在"));
        if (!alarm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此闹钟");
        }
        alarmRepository.delete(alarm);
    }

    public Alarm getById(Integer alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("闹钟不存在"));
    }

    private void validate(AlarmRequest request) {
        if (!request.repeatDays().contains("1")) {
            throw new IllegalArgumentException("请至少选择一天重复");
        }
    }

    private Alarm mapToEntity(Alarm alarm, AlarmRequest request) {
        alarm.setUserId(request.userId());
        alarm.setAlarmTime(request.alarmTime());
        alarm.setRepeatDays(request.repeatDays());
        alarm.setRingtone(request.ringtone() != null && !request.ringtone().isBlank()
                ? request.ringtone() : AppConstants.RINGTONES[0]);
        alarm.setActive(request.active());
        alarm.setLabel(request.label());
        return alarm;
    }
}
