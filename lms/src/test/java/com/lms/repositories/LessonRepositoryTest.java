package com.lms.repositories;

import com.lms.entities.Course;
import com.lms.entities.Lesson;
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
class LessonRepositoryTest {

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
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldSaveLesson() {

        Course course = courseRepository.save(
                Course.builder()
                        .title("Spring Boot")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        Lesson lesson = Lesson.builder()
                .title("Intro")
                .orderIndex(1)
                .course(course)
                .build();

        Lesson saved = lessonRepository.save(lesson);

        assertNotNull(saved.getId());
    }

    @Test
    void shouldFindLessonsByCourseOrdered() {

        Course course = courseRepository.save(
                Course.builder()
                        .title("Java")
                        .status("PUBLISHED")
                        .active(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build()
        );

        lessonRepository.save(
                Lesson.builder()
                        .title("Lesson 2")
                        .orderIndex(2)
                        .course(course)
                        .build()
        );

        lessonRepository.save(
                Lesson.builder()
                        .title("Lesson 1")
                        .orderIndex(1)
                        .course(course)
                        .build()
        );

        List<Lesson> lessons =
                lessonRepository.FindByCourseIdOrderByOrderIndex(course.getId());

        assertEquals(2, lessons.size());
        assertEquals(1, lessons.get(0).getOrderIndex());
        assertEquals(2, lessons.get(1).getOrderIndex());
    }
}