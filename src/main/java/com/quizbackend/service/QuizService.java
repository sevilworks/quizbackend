package com.quizbackend.service;

import com.quizbackend.entity.*;
import com.quizbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    public Quiz createQuiz(Quiz quiz, Integer professorId) {
        // Generate unique quiz code
        String code = generateUniqueQuizCode();
        quiz.setCode(code);
        quiz.setProfessorId(professorId);
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Integer quizId, Quiz updatedQuiz, Integer professorId) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!existingQuiz.getProfessorId().equals(professorId)) {
            throw new RuntimeException("Unauthorized to update this quiz");
        }

        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        existingQuiz.setDuration(updatedQuiz.getDuration());

        return quizRepository.save(existingQuiz);
    }

    public void deleteQuiz(Integer quizId, Integer professorId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (professorId != null && !quiz.getProfessorId().equals(professorId)) {
            throw new RuntimeException("Unauthorized to delete this quiz");
        }

        quizRepository.delete(quiz);
    }

    public List<Quiz> getQuizzesByProfessor(Integer professorId) {
        return quizRepository.findByProfessorId(professorId);
    }

    public Optional<Quiz> getQuizByCode(String code) {
        return quizRepository.findByCode(code);
    }

    public Quiz getQuizById(Integer quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public Question addQuestion(Integer quizId, Question question, Integer professorId) {
        Quiz quiz = getQuizById(quizId);
        
        if (!quiz.getProfessorId().equals(professorId)) {
            throw new RuntimeException("Unauthorized to add questions to this quiz");
        }

        question.setQuizId(quizId);
        return questionRepository.save(question);
    }

    public Response addResponse(Integer questionId, Response response, Integer professorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Quiz quiz = question.getQuiz();
        if (!quiz.getProfessorId().equals(professorId)) {
            throw new RuntimeException("Unauthorized to add responses to this question");
        }

        response.setQuestionId(questionId);
        return responseRepository.save(response);
    }

    public Participation submitQuizAnswers(Integer quizId, List<Integer> selectedResponseIds, 
                                         Integer userId, Integer guestId) {
        Quiz quiz = getQuizById(quizId);
        
        // Check if user already participated
        if (userId != null && participationRepository.existsByQuizIdAndUserId(quizId, userId)) {
            throw new RuntimeException("User has already participated in this quiz");
        }
        if (guestId != null && participationRepository.existsByQuizIdAndGuestId(quizId, guestId)) {
            throw new RuntimeException("Guest has already participated in this quiz");
        }

        // Calculate score
        BigDecimal score = calculateScore(quizId, selectedResponseIds);

        // Create participation record
        Participation participation = new Participation();
        participation.setQuizId(quizId);
        participation.setUserId(userId);
        participation.setGuestId(guestId);
        participation.setScore(score);

        return participationRepository.save(participation);
    }

    public Participation registerParticipationByCode(String code, Integer userId, Integer guestId) {
        Quiz quiz = quizRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Integer quizId = quiz.getId();

        if (userId != null && participationRepository.existsByQuizIdAndUserId(quizId, userId)) {
            throw new RuntimeException("User has already participated in this quiz");
        }
        if (guestId != null && participationRepository.existsByQuizIdAndGuestId(quizId, guestId)) {
            throw new RuntimeException("Guest has already participated in this quiz");
        }

        Participation participation = new Participation();
        participation.setQuizId(quizId);
        participation.setUserId(userId);
        participation.setGuestId(guestId);
        participation.setScore(java.math.BigDecimal.ZERO);

        return participationRepository.save(participation);
    }

    public List<Participation> getQuizParticipations(Integer quizId, Integer professorId) {
        Quiz quiz = getQuizById(quizId);
        
        if (!quiz.getProfessorId().equals(professorId)) {
            throw new RuntimeException("Unauthorized to view participations for this quiz");
        }

        return participationRepository.findByQuizId(quizId);
    }

    public List<Participation> getUserParticipations(Integer userId) {
        return participationRepository.findByUserId(userId);
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    private String generateUniqueQuizCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (quizRepository.existsByCode(code));
        return code;
    }

    private BigDecimal calculateScore(Integer quizId, List<Integer> selectedResponseIds) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        for (Question question : questions) {
            List<Response> correctResponses = responseRepository.findByQuestionIdAndIsCorrectTrue(question.getId());
            List<Response> selectedResponses = responseRepository.findAllById(selectedResponseIds);
            
            // Check if all correct responses are selected and no incorrect ones
            boolean isCorrect = correctResponses.size() == selectedResponses.size() &&
                    selectedResponses.stream().allMatch(Response::getIsCorrect) &&
                    selectedResponses.stream().allMatch(r -> r.getQuestionId().equals(question.getId()));

            if (isCorrect) {
                correctAnswers++;
            }
        }

        if (totalQuestions == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(correctAnswers)
                .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
