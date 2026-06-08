package com.funalarm.service;

import com.funalarm.config.AppConstants;
import com.funalarm.dto.SubmitAnswerRequest;
import com.funalarm.entity.*;
import com.funalarm.repository.AnswerAttemptRepository;
import com.funalarm.repository.WakeSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class QuizService {
    private final WakeSessionRepository wakeSessionRepository;
    private final AnswerAttemptRepository answerAttemptRepository;
    private final AlarmService alarmService;
    private final QuestionService questionService;

    public QuizService(WakeSessionRepository wakeSessionRepository,
                       AnswerAttemptRepository answerAttemptRepository,
                       AlarmService alarmService,
                       QuestionService questionService) {
        this.wakeSessionRepository = wakeSessionRepository;
        this.answerAttemptRepository = answerAttemptRepository;
        this.alarmService = alarmService;
        this.questionService = questionService;
    }

    @Transactional
    public Map<String, Object> startSession(Integer alarmId) {
        Alarm alarm = alarmService.getById(alarmId);
        LocalDate today = LocalDate.now();
        if (wakeSessionRepository.existsByAlarmIdAndSessionDate(alarmId, today)) {
            throw new IllegalArgumentException("该闹钟今日已响铃");
        }
        WakeSession session = new WakeSession();
        session.setUserId(alarm.getUserId());
        session.setAlarmId(alarm.getAlarmId());
        session.setSessionDate(today);
        session.setAlarmTime(alarm.getAlarmTime());
        session.setTriggerAt(LocalDateTime.now());
        session = wakeSessionRepository.save(session);
        Question question = questionService.getRandom();
        return Map.of("session", session, "question", question, "alarm", alarm);
    }

    @Transactional
    public Map<String, Object> submitAnswer(SubmitAnswerRequest request) {
        WakeSession session = wakeSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("会话不存在"));
        Question question = questionService.getById(request.questionId());
        char userAnswer = Character.toUpperCase(request.userAnswer().charAt(0));
        boolean correct = userAnswer == question.getAnswer().charAt(0);

        AnswerAttempt attempt = new AnswerAttempt();
        attempt.setSessionId(session.getSessionId());
        attempt.setQuestionId(question.getQuestionId());
        attempt.setUserAnswer(String.valueOf(userAnswer));
        attempt.setCorrect(correct);
        answerAttemptRepository.save(attempt);

        if (correct) {
            session.setSuccess(true);
            session.setSuccessAt(LocalDateTime.now());
            wakeSessionRepository.save(session);
            return Map.of(
                    "correct", true,
                    "switchedQuestion", false,
                    "session", session,
                    "streakDays", 0
            );
        }

        int wrongCount = session.getWrongCount() + 1;
        session.setWrongCount(wrongCount);

        if (wrongCount >= AppConstants.WRONG_BEFORE_SWITCH) {
            Question next = questionService.getRandomExcept(question.getQuestionId());
            session.setWrongCount(0);
            wakeSessionRepository.save(session);
            String message = "已连续答错 3 次，正确答案是 " + question.getAnswer()
                    + "：" + getOptionText(question, question.getAnswer()) + "，已换题";
            return Map.of(
                    "correct", false,
                    "switchedQuestion", true,
                    "session", session,
                    "previousQuestion", question,
                    "nextQuestion", next,
                    "message", message
            );
        }

        wakeSessionRepository.save(session);
        return Map.of(
                "correct", false,
                "switchedQuestion", false,
                "session", session,
                "wrongCount", wrongCount,
                "message", "答错了，请重试！（已连续错 " + wrongCount + " 次）"
        );
    }

    private String getOptionText(Question q, String answer) {
        return switch (answer.toUpperCase()) {
            case "A" -> q.getOptionA();
            case "B" -> q.getOptionB();
            case "C" -> q.getOptionC();
            case "D" -> q.getOptionD();
            default -> "";
        };
    }
}
