package com.lms.services;

import com.lms.entities.Student;
import com.lms.repositories.StudentRepository;
import com.lms.repositories.AssessmentRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.services.StudentService;

import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssessmentRepository assessmentRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository,
                              AssessmentRepository assessmentRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    public Student create(Student student) {

        if (student == null)
            throw new StudentInvalidException("Student null");

        if (student.getEmail() == null || student.getEmail().isBlank())
            throw new StudentInvalidException("Email requerido");

        if (existsByEmail(student.getEmail()))
            throw new DuplicateStudentEmailException("Email duplicado");

        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, Student student) {

        Student existing = findById(id);

        if (student.getEmail() != null && !student.getEmail().equals(existing.getEmail())) {
            if (existsByEmail(student.getEmail()))
                throw new DuplicateStudentEmailException("Email duplicado");
            existing.setEmail(student.getEmail());
        }

        if (student.getFullName() != null)
            existing.setFullName(student.getFullName());

        return studentRepository.save(existing);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student no encontrado"));
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAllStudents();
    }

    @Override
    public void delete(Long id) {

        if (hasEnrollments(id))
            throw new StudentConflictException("No se puede eliminar estudiante con inscripciones");

        studentRepository.delete(findById(id));
    }

    @Override
    public Student findByEmail(String email) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new StudentNotFoundException("No encontrado"));
    }

    @Override
    public List<Student> findByCourse(Long courseId) {
        return studentRepository.findStudentsByCourseId(courseId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.findAll().stream()
                .anyMatch(s -> s.getEmail().equals(email));
    }

    @Override
    public boolean hasEnrollments(Long studentId) {
        return enrollmentRepository.findAll().stream()
                .anyMatch(e -> e.getStudent().getId().equals(studentId));
    }

    @Override
    public long countEnrollments(Long studentId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .count();
    }

    @Override
    public double calculateAverageScore(Long studentId) {
        var list = assessmentRepository.findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .toList();

        if (list.isEmpty())
            return 0;

        return list.stream().mapToInt(a -> a.getScore()).average().orElse(0);
    }
}