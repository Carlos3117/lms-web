package com.lms.repositories;

import com.lms.entities.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "logging.level.org.hibernate.SQL=DEBUG\n" ,
                "logging.level.org.hibernate.orm.jdbc.bind=TRACE"
})
class CourseRepositoryTest {

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
    private CourseRepository courseRepository;

    @Test
    void shouldSaveCourse(){

        // GIVEN
        Course course = Course.builder()
                .title("Spring Boot")
                .status("PUBLISHED")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        // WHEN
        Course savedCourse = courseRepository.save(course);

        // THEN
        assertNotNull(savedCourse.getId());
    }

    @Test
    void shouldFindCourseById(){

        // GIVEN
        Course course = Course.builder()
                .title("Java Course")
                .status("PUBLISHED")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Course savedCourse = courseRepository.save(course);

        // WHEN
        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElse(null);

        // THEN
        assertNotNull(foundCourse);
        assertEquals("Java Course", foundCourse.getTitle());
    }

    @Test
    void shouldUpdateCourse(){

        // GIVEN
        Course course = Course.builder()
                .title("Old title")
                .status("PUBLISHED")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Course savedCourse = courseRepository.save(course);

        // WHEN
        savedCourse.setTitle("New title");
        Course updatedCourse = courseRepository.save(savedCourse);

        // THEN
        assertEquals("New title", updatedCourse.getTitle());
    }

    @Test
    void shouldDeleteCourse(){

        // GIVEN
        Course course = Course.builder()
                .title("Delete me")
                .status("PUBLISHED")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Course savedCourse = courseRepository.save(course);

        // WHEN
        courseRepository.deleteById(savedCourse.getId());

        // THEN
        boolean exists = courseRepository.findById(savedCourse.getId()).isPresent();
        assertFalse(exists);
    }
}