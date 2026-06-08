package com.funalarm.repository;

import com.funalarm.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandom();

    @Query(value = "SELECT * FROM questions WHERE question_id <> :excludeId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomExcept(Integer excludeId);
}
