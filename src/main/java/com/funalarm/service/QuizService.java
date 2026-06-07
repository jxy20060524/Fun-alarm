package com.funalarm.service;

import com.funalarm.dao.WakeDAO;
import com.funalarm.model.Alarm;
import com.funalarm.model.AnswerAttempt;
import com.funalarm.model.Question;
import com.funalarm.model.WakeSession;
import com.funalarm.util.AppConstants;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuizService {
    private final WakeDAO wakeDAO = new WakeDAO();
    private final QuestionService questionService = new QuestionService();

    public WakeSession startSession(Alarm alarm) throws SQLException {
        WakeSession session = new WakeSession();
        session.setUserId(alarm.getUserId());
        session.setAlarmId(alarm.getAlarmId());
        session.setSessionDate(LocalDate.now());
        session.setAlarmTime(alarm.getAlarmTime());
        session.setTriggerAt(LocalDateTime.now());
        return wakeDAO.insertSession(session);
    }

    public SubmitResult submitAnswer(int sessionId, int questionId, char userAnswer) throws SQLException {
        WakeSession session = wakeDAO.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("会话不存在"));
        Question question = new com.funalarm.dao.QuestionDAO().findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("题目不存在"));

        boolean correct = Character.toUpperCase(userAnswer) == Character.toUpperCase(question.getAnswer());

        AnswerAttempt attempt = new AnswerAttempt();
        attempt.setSessionId(sessionId);
        attempt.setQuestionId(questionId);
        attempt.setUserAnswer(Character.toUpperCase(userAnswer));
        attempt.setCorrect(correct);
        wakeDAO.insertAttempt(attempt);

        if (correct) {
            LocalDateTime now = LocalDateTime.now();
            wakeDAO.markSuccess(sessionId, now);
            session.setSuccess(true);
            session.setSuccessAt(now);
            return SubmitResult.success(session);
        }

        int wrongCount = session.getWrongCount() + 1;
        wakeDAO.updateWrongCount(sessionId, wrongCount);
        session.setWrongCount(wrongCount);

        if (shouldSwitchQuestion(session)) {
            Question next = questionService.getRandomQuestionExcept(questionId);
            wakeDAO.updateWrongCount(sessionId, 0);
            session.setWrongCount(0);
            return SubmitResult.switchQuestion(session, question, next);
        }

        return SubmitResult.wrong(session, wrongCount);
    }

    public boolean shouldSwitchQuestion(WakeSession session) {
        return session.getWrongCount() >= AppConstants.WRONG_BEFORE_SWITCH;
    }

    public record SubmitResult(
            boolean correct,
            boolean switchedQuestion,
            WakeSession session,
            Question previousQuestion,
            Question nextQuestion,
            int wrongCount,
            String message
    ) {
        static SubmitResult success(WakeSession session) {
            return new SubmitResult(true, false, session, null, null, 0, null);
        }

        static SubmitResult wrong(WakeSession session, int wrongCount) {
            return new SubmitResult(false, false, session, null, null, wrongCount,
                    "答错了，请重试！（已连续错 " + wrongCount + " 次）");
        }

        static SubmitResult switchQuestion(WakeSession session, Question previous, Question next) {
            return new SubmitResult(false, true, session, previous, next, 0,
                    "已连续答错 3 次，正确答案是 " + previous.getAnswer()
                            + "：" + previous.getOptionText(previous.getAnswer()) + "，已换题");
        }
    }
}
