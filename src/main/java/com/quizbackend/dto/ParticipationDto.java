package com.quizbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParticipationDto {
    private Integer id;
    private BigDecimal score;
    private LocalDateTime createdAt;
    private QuizSummary quiz;
    private Integer userId;
    private Integer guestId;

    @Data
    public static class QuizSummary {
        private Integer id;
        private String title;
        private String code;
    }

    public static ParticipationDto fromEntity(com.quizbackend.entity.Participation p) {
        if (p == null) return null;
        ParticipationDto dto = new ParticipationDto();
        dto.setId(p.getId());
        dto.setScore(p.getScore());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUserId(p.getUserId());
        dto.setGuestId(p.getGuestId());

        com.quizbackend.entity.Quiz q = p.getQuiz();
        if (q != null) {
            QuizSummary qs = new QuizSummary();
            qs.setId(q.getId());
            qs.setTitle(q.getTitle());
            qs.setCode(q.getCode());
            dto.setQuiz(qs);
        }

        return dto;
    }
}
