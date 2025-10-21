package com.quizbackend.service;

import com.quizbackend.entity.Student;
import com.quizbackend.entity.User;
import com.quizbackend.repository.StudentRepository;
import com.quizbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    public Student createStudent(User user, String firstName, String lastName) {
        Student student = new Student();
        student.setId(user.getId());
        student.setUserId(user.getId());
        student.setUsername(user.getUsername());
        student.setEmail(user.getEmail());
        student.setPassword(user.getPassword());
        student.setRole(user.getRole());
        student.setCreatedAt(user.getCreatedAt());
        student.setFirstName(firstName);
        student.setLastName(lastName);
        
        return studentRepository.save(student);
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
