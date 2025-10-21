package com.quizbackend.repository;

import com.quizbackend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    List<Quiz> findByProfessorId(Integer professorId);
    Optional<Quiz> findByCode(String code);
    boolean existsByCode(String code);
}
