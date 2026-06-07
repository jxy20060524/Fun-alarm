package com.funalarm.dao;

import com.funalarm.config.DatabaseConfig;
import com.funalarm.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuestionDAO {

    public Optional<Question> findById(int questionId) throws SQLException {
        String sql = """
                SELECT question_id, content, option_a, option_b, option_c, option_d, answer, difficulty
                FROM questions WHERE question_id = ?
                """;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Question findRandom() throws SQLException {
        List<Question> all = findAll();
        if (all.isEmpty()) {
            throw new SQLException("题库为空，请先执行 sql/init.sql");
        }
        return all.get(ThreadLocalRandom.current().nextInt(all.size()));
    }

    public Question findRandomExcept(int excludeId) throws SQLException {
        List<Question> all = findAll();
        all.removeIf(q -> q.getQuestionId() == excludeId);
        if (all.isEmpty()) {
            return findRandom();
        }
        return all.get(ThreadLocalRandom.current().nextInt(all.size()));
    }

    private List<Question> findAll() throws SQLException {
        String sql = """
                SELECT question_id, content, option_a, option_b, option_c, option_d, answer, difficulty
                FROM questions
                """;
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                questions.add(mapRow(rs));
            }
        }
        return questions;
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getInt("question_id"));
        q.setContent(rs.getString("content"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        String answer = rs.getString("answer");
        q.setAnswer(answer != null && !answer.isEmpty() ? answer.charAt(0) : 'A');
        q.setDifficulty(rs.getInt("difficulty"));
        return q;
    }
}
