package com.funalarm.service;

import com.funalarm.dao.QuestionDAO;
import com.funalarm.model.Question;

import java.sql.SQLException;

public class QuestionService {
    private final QuestionDAO questionDAO = new QuestionDAO();

    public Question getRandomQuestion() throws SQLException {
        return questionDAO.findRandom();
    }

    public Question getRandomQuestionExcept(int excludeId) throws SQLException {
        return questionDAO.findRandomExcept(excludeId);
    }
}
