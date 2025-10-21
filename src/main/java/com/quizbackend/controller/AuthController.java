package com.quizbackend.controller;

import com.quizbackend.entity.User;
import com.quizbackend.service.AuthService;
import com.quizbackend.service.ProfessorService;
import com.quizbackend.service.StudentService;
import com.quizbackend.service.AdminService;
import com.quizbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> response = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegisterRequest request) {
        try {
            User user = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                User.Role.STUDENT,
                request.getFirstName(),
                request.getLastName()
            );
            
            studentService.createStudent(user, request.getFirstName(), request.getLastName());
            
            return ResponseEntity.ok(Map.of("message", "Student registered successfully", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/professor")
    public ResponseEntity<?> registerProfessor(@RequestBody ProfessorRegisterRequest request) {
        try {
            User user = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                User.Role.PROFESSOR_FREE,
                request.getFirstName(),
                request.getLastName()
            );
            
            professorService.createProfessor(user, request.getFirstName(), request.getLastName());
            
            return ResponseEntity.ok(Map.of("message", "Professor registered successfully", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = extractUsernameFromToken(token);
            User user = authService.getCurrentUser(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsernameFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUsernameFromToken(token);
    }

    // Request DTOs
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class StudentRegisterRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class ProfessorRegisterRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
}
