package com.quizbackend.service;

import com.quizbackend.entity.Student;
import com.quizbackend.entity.User;
import com.quizbackend.repository.StudentRepository;
import com.quizbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    public Student createStudent(User user, String firstName, String lastName) {
        logger.info("Creating Student entity for userId={}, firstName={}, lastName={}", user.getId(), firstName, lastName);

        Student student = new Student();
        student.setUserId(user.getId());
        student.setFirstName(firstName);
        student.setLastName(lastName);
        
        Student saved = studentRepository.save(student);
        logger.info("Saved Student: {}", saved);
        return saved;
    }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student updateStudent(Integer id, String firstName, String lastName) {
        Student student = getStudentById(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
