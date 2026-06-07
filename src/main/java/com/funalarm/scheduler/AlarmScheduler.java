package com.funalarm.scheduler;

import com.funalarm.model.Alarm;
import com.funalarm.model.Question;
import com.funalarm.model.User;
import com.funalarm.model.WakeSession;
import com.funalarm.service.AlarmService;
import com.funalarm.service.QuestionService;
import com.funalarm.service.QuizService;
import com.funalarm.dao.WakeDAO;
import javafx.application.Platform;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class AlarmScheduler {
    private final AlarmService alarmService = new AlarmService();
    private final QuizService quizService = new QuizService();
    private final QuestionService questionService = new QuestionService();
    private final WakeDAO wakeDAO = new WakeDAO();

    private ScheduledExecutorService executor;
    private User currentUser;
    private List<Alarm> alarms = List.of();
    private BiConsumer<Alarm, RingContext> ringHandler;

    public void setRingHandler(BiConsumer<Alarm, RingContext> ringHandler) {
        this.ringHandler = ringHandler;
    }

    public void start(User user) {
        this.currentUser = user;
        reloadAlarms();
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "alarm-scheduler");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::checkAlarms, 0, 1, TimeUnit.SECONDS);
    }

    public void reloadAlarms() {
        if (currentUser == null) {
            return;
        }
        try {
            alarms = alarmService.listActiveByUser(currentUser.getUserId());
        } catch (SQLException e) {
            alarms = List.of();
        }
    }

    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private void checkAlarms() {
        if (currentUser == null || ringHandler == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        for (Alarm alarm : alarms) {
            if (!alarm.isActive()) {
                continue;
            }
            if (!alarm.isRepeatOnDay(today.getDayOfWeek().getValue())) {
                continue;
            }
            if (!alarm.getAlarmTime().equals(now)) {
                continue;
            }
            try {
                if (wakeDAO.hasTriggeredToday(alarm.getAlarmId(), today)) {
                    continue;
                }
                WakeSession session = quizService.startSession(alarm);
                Question question = questionService.getRandomQuestion();
                Platform.runLater(() -> ringHandler.accept(alarm, new RingContext(session, question)));
            } catch (SQLException ignored) {
            }
        }
    }

    public record RingContext(WakeSession session, Question question) {
    }
}
