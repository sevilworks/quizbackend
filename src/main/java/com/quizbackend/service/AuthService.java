package com.quizbackend.service;

import com.quizbackend.entity.User;
import com.quizbackend.repository.UserRepository;
import com.quizbackend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            logger.warn("Invalid login attempt for username={}", username);
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        response.put("role", user.getRole().name());

        logger.info("User logged in: username={}, role={}", user.getUsername(), user.getRole());

        return response;
    }

    public User register(String username, String email, String password, User.Role role, String firstName, String lastName) {
        logger.info("Registering user: username={}, email={}, role={}, firstName={}, lastName={}", username, email, role, firstName, lastName);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Username already exists: {}", username);
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Email already exists: {}", email);
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        logger.info("User saved to DB: id={}, username={}", savedUser.getId(), savedUser.getUsername());

        // Create specific user type based on role
        switch (role) {
            case PROFESSOR_FREE, PROFESSOR_VIP -> {
                // Professor creation will be handled in ProfessorService
            }
            case STUDENT -> {
                // Student creation will be handled in StudentService
            }
            case ADMIN -> {
                // Admin creation will be handled in AdminService
            }
        }

        return savedUser;
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
