package com.funalarm.controller;

import com.funalarm.config.AppConstants;
import com.funalarm.dto.*;
import com.funalarm.entity.User;
import com.funalarm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.username());
    }
}

@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}/nickname")
    public User updateNickname(@PathVariable Integer userId, @Valid @RequestBody NicknameRequest request) {
        return userService.updateNickname(userId, request.nickname());
    }
}

@RestController
@RequestMapping("/api/alarms")
class AlarmController {
    private final com.funalarm.service.AlarmService alarmService;

    AlarmController(com.funalarm.service.AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping
    public List<com.funalarm.entity.Alarm> list(@RequestParam Integer userId) {
        return alarmService.listByUser(userId);
    }

    @GetMapping("/active")
    public List<com.funalarm.entity.Alarm> listActive(@RequestParam Integer userId) {
        return alarmService.listActiveByUser(userId);
    }

    @PostMapping
    public com.funalarm.entity.Alarm create(@Valid @RequestBody AlarmRequest request) {
        return alarmService.create(request);
    }

    @PutMapping("/{id}")
    public com.funalarm.entity.Alarm update(@PathVariable Integer id, @Valid @RequestBody AlarmRequest request) {
        return alarmService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Integer id, @RequestParam Integer userId) {
        alarmService.delete(id, userId);
        return Map.of("message", "删除成功");
    }
}

@RestController
@RequestMapping("/api/quiz")
class QuizController {
    private final com.funalarm.service.QuizService quizService;
    private final com.funalarm.service.StatsService statsService;

    QuizController(com.funalarm.service.QuizService quizService,
                   com.funalarm.service.StatsService statsService) {
        this.quizService = quizService;
        this.statsService = statsService;
    }

    @PostMapping("/sessions")
    public Map<String, Object> startSession(@Valid @RequestBody StartSessionRequest request) {
        return quizService.startSession(request.alarmId());
    }

    @PostMapping("/submit")
    public Map<String, Object> submit(@Valid @RequestBody SubmitAnswerRequest request) {
        Map<String, Object> result = new java.util.HashMap<>(quizService.submitAnswer(request));
        if (Boolean.TRUE.equals(result.get("correct"))) {
            com.funalarm.entity.WakeSession session = (com.funalarm.entity.WakeSession) result.get("session");
            result.put("streakDays", statsService.getStreakDays(session.getUserId()));
            result.put("motivation", statsService.randomMotivation());
        }
        return result;
    }
}

@RestController
@RequestMapping("/api/stats")
class StatsController {
    private final com.funalarm.service.StatsService statsService;

    StatsController(com.funalarm.service.StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/today")
    public Map<String, Object> today(@RequestParam Integer userId) {
        return Map.of("wakeTime", statsService.getTodayWakeTime(userId).orElse(null));
    }

    @GetMapping("/streak")
    public Map<String, Integer> streak(@RequestParam Integer userId) {
        return Map.of("streakDays", statsService.getStreakDays(userId));
    }

    @GetMapping("/recent")
    public List<com.funalarm.entity.WakeSession> recent(@RequestParam Integer userId,
                                                      @RequestParam(defaultValue = "10") int limit) {
        return statsService.getRecentSessions(userId, limit);
    }
}

@RestController
@RequestMapping("/api/meta")
class MetaController {
    private final com.funalarm.service.StatsService statsService;

    MetaController(com.funalarm.service.StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/ringtones")
    public List<String> ringtones() {
        return Arrays.asList(AppConstants.RINGTONES);
    }

    @GetMapping("/motivation")
    public Map<String, String> motivation() {
        return Map.of("message", statsService.randomMotivation());
    }
}
