package com.quizbackend.controller;

import com.quizbackend.entity.Quiz;
import com.quizbackend.entity.Question;
import com.quizbackend.entity.Response;
import com.quizbackend.entity.Participation;
import com.quizbackend.entity.Professor;
import com.quizbackend.service.QuizService;
import com.quizbackend.service.AuthService;
import com.quizbackend.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProfessorService professorService;

    // Helper method to get professor ID from username
    private Integer getProfessorId(String username) {
        // First get the user
        Integer userId = authService.getCurrentUser(username).getId();
        // Then get the professor using the user ID
        
        
        /*Professor professor = professorService.getProfessorById(userId);
        return professor.getId();*/
        Professor professor = professorService.getProfessorByUsername(username);
        return professor.getUserId();
    }

    // Professor endpoints
    @PostMapping("/create")
    public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz, Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            Quiz createdQuiz = quizService.createQuiz(quiz, professorId);
            return ResponseEntity.ok(createdQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Integer quizId, @RequestBody Quiz quiz, Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            Quiz updatedQuiz = quizService.updateQuiz(quizId, quiz, professorId);
            return ResponseEntity.ok(updatedQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Integer quizId, Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            quizService.deleteQuiz(quizId, professorId);
            return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-quizzes")
    public ResponseEntity<?> getMyQuizzes(Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            List<Quiz> quizzes = quizService.getQuizzesByProfessor(professorId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
@PostMapping("/{quizId}/questions")
public ResponseEntity<?> addQuestion(@PathVariable Integer quizId, @RequestBody Map<String, Object> rawJson, Authentication authentication) {
    System.out.println("Raw JSON received: " + rawJson);
    // Convert to Question manually
    Question question = new Question();
    question.setQuestionText((String) rawJson.get("questionText"));
    question.setQuizId(quizId);
    
    try {
        String username = authentication.getName();
        Integer professorId = getProfessorId(username);
        Question createdQuestion = quizService.addQuestion(quizId, question, professorId);
        return ResponseEntity.ok(createdQuestion);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
    @PostMapping("/questions/{questionId}/responses")
    public ResponseEntity<?> addResponse(@PathVariable Integer questionId, @RequestBody Response response, Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            Response createdResponse = quizService.addResponse(questionId, response, professorId);
            return ResponseEntity.ok(createdResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{quizId}/participations")
    public ResponseEntity<?> getQuizParticipations(@PathVariable Integer quizId, Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer professorId = getProfessorId(username);
            
            List<Participation> participations = quizService.getQuizParticipations(quizId, professorId);
            return ResponseEntity.ok(participations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Public endpoints for joining quizzes
    @GetMapping("/join/{code}")
    public ResponseEntity<?> getQuizByCode(@PathVariable String code) {
        try {
            return quizService.getQuizByCode(code)
                    .map(quiz -> ResponseEntity.ok(quiz))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Integer quizId, @RequestBody QuizSubmissionRequest request, Authentication authentication) {
        try {
            Integer userId = null;
            if (authentication != null) {
                String username = authentication.getName();
                userId = authService.getCurrentUser(username).getId();
            }
            
            Participation participation = quizService.submitQuizAnswers(quizId, request.getSelectedResponseIds(), userId, request.getGuestId());
            return ResponseEntity.ok(participation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizById(@PathVariable Integer quizId) {
        try {
            Quiz quiz = quizService.getQuizById(quizId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Student endpoints
    @GetMapping("/my-participations")
    public ResponseEntity<?> getMyParticipations(Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer userId = authService.getCurrentUser(username).getId();
            
            List<Participation> participations = quizService.getUserParticipations(userId);
            return ResponseEntity.ok(participations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Request DTOs
    public static class QuizSubmissionRequest {
        private List<Integer> selectedResponseIds;
        private Integer guestId;

        public List<Integer> getSelectedResponseIds() { return selectedResponseIds; }
        public void setSelectedResponseIds(List<Integer> selectedResponseIds) { this.selectedResponseIds = selectedResponseIds; }
        public Integer getGuestId() { return guestId; }
        public void setGuestId(Integer guestId) { this.guestId = guestId; }
    }
}