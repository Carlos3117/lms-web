package com.lms.repositories;

import com.lms.entities.Assessment;
import com.lms.entities.Course;
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
class AssessmentRepositoryTest {

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
    private AssessmentRepository assessmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldSaveAssessment() {

        Student student = studentRepository.save(
                Student.builder()
                        .fullName("Carlos Perez")
                        .email("carlos@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        Course course = courseRepository.save(
                Course.builder()
                        .title("Spring")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        Assessment assessment = Assessment.builder()
                .type("EXAM")
                .student(student)
                .course(course)
                .score(85)
                .takenAt(Instant.now())
                .build();

        Assessment saved = assessmentRepository.save(assessment);

        assertNotNull(saved.getId());
    }

    @Test
    void shouldFindByStudentAndDateRange() {

        Student student = studentRepository.save(
                Student.builder()
                        .fullName("Juan Lopez")
                        .email("juan@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        Course course = courseRepository.save(
                Course.builder()
                        .title("Java")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        Instant now = Instant.now();

        assessmentRepository.save(
                Assessment.builder()
                        .type("QUIZ")
                        .student(student)
                        .course(course)
                        .score(80)
                        .takenAt(now.minusSeconds(1000))
                        .build()
        );

        assessmentRepository.save(
                Assessment.builder()
                        .type("EXAM")
                        .student(student)
                        .course(course)
                        .score(90)
                        .takenAt(now.plusSeconds(1000))
                        .build()
        );

        List<Assessment> result =
                assessmentRepository.findAssessmentByStudendIdAndBetweenTakenAt(
                        student.getId(),
                        now.minusSeconds(2000),
                        now
                );

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByStudentAndScoreRange() {

        Student student = studentRepository.save(
                Student.builder()
                        .fullName("Maria Gomez")
                        .email("maria@test.com")
                        .createdAt(Instant.now())
                        .build()
        );

        Course course = courseRepository.save(
                Course.builder()
                        .title("Python")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        assessmentRepository.save(
                Assessment.builder()
                        .type("QUIZ")
                        .student(student)
                        .course(course)
                        .score(50)
                        .takenAt(Instant.now())
                        .build()
        );

        assessmentRepository.save(
                Assessment.builder()
                        .type("EXAM")
                        .student(student)
                        .course(course)
                        .score(85)
                        .takenAt(Instant.now())
                        .build()
        );

        List<Assessment> result =
                assessmentRepository.findAssessmentByStudentIdAndBetweenScore(
                        student.getId(),
                        80,
                        100
                );

        assertEquals(1, result.size());
        assertEquals(85, result.get(0).getScore());
    }
}