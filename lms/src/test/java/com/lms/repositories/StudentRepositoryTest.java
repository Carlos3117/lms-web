package com.lms.repositories;

import com.lms.entities.Course;
import com.lms.entities.Enrollment;
import com.lms.entities.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class StudentRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void shouldSaveStudent() {

        Student student = studentRepository.save(
                Student.builder()
                        .fullName("Carlos Perez")
                        .email("carlos@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        assertNotNull(student.getId());
    }

    @Test
    void shouldFindAllStudents() {

        studentRepository.save(
                Student.builder()
                        .fullName("A")
                        .email("a@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        studentRepository.save(
                Student.builder()
                        .fullName("B")
                        .email("b@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        List<Student> students = studentRepository.findAllStudents();

        assertEquals(2, students.size());
    }

    @Test
    void shouldFindStudentsByCourseId() {

        Course course = courseRepository.save(
                Course.builder()
                        .title("Spring")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        Student student1 = studentRepository.save(
                Student.builder()
                        .fullName("Juan Lopez")
                        .email("juan@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        Student student2 = studentRepository.save(
                Student.builder()
                        .fullName("Maria Gomez")
                        .email("maria@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        // solo uno inscrito al curso
        enrollmentRepository.save(
                Enrollment.builder()
                        .status("ACTIVE")
                        .enrolledAt(Instant.now())
                        .course(course)
                        .student(student1)
                        .build()
        );

        List<Student> result =
                studentRepository.findStudentsByCourseId(course.getId());

        assertEquals(1, result.size());
        assertEquals("Juan Lopez", result.get(0).getFullName());
    }
}