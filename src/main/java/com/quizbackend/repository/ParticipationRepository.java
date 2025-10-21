package com.quizbackend.repository;

import com.quizbackend.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Integer> {
    List<Participation> findByQuizId(Integer quizId);
    List<Participation> findByUserId(Integer userId);
    List<Participation> findByGuestId(Integer guestId);
    boolean existsByQuizIdAndUserId(Integer quizId, Integer userId);
    boolean existsByQuizIdAndGuestId(Integer quizId, Integer guestId);
}
