package com.funalarm.service;

import com.funalarm.entity.Question;
import com.funalarm.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question getRandom() {
        return questionRepository.findRandom()
                .orElseThrow(() -> new IllegalArgumentException("题库为空"));
    }

    public Question getRandomExcept(Integer excludeId) {
        return questionRepository.findRandomExcept(excludeId)
                .or(() -> questionRepository.findRandom())
                .orElseThrow(() -> new IllegalArgumentException("题库为空"));
    }

    public Question getById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("题目不存在"));
    }
}
