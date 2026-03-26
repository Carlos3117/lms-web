package com.lms.services.impl;

import com.lms.entities.Lesson;
import com.lms.repositories.LessonRepository;
import com.lms.repositories.CourseRepository;
import com.lms.services.LessonService;

import java.util.List;

public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonServiceImpl(LessonRepository lessonRepository,
                             CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Lesson create(Lesson lesson) {

        if (lesson == null)
            throw new LessonInvalidException("Lesson null");

        if (lesson.getTitle() == null || lesson.getTitle().isBlank())
            throw new LessonInvalidException("Título requerido");

        if (lesson.getCourse() == null || lesson.getCourse().getId() == null)
            throw new LessonInvalidException("Curso requerido");

        courseRepository.findById(lesson.getCourse().getId())
                .orElseThrow(() -> new LessonInvalidException("Curso no existe"));

        if (lesson.getOrderIndex() < 0)
            throw new LessonInvalidException("Orden inválido");

        return lessonRepository.save(lesson);
    }

    @Override
    public Lesson update(Long id, Lesson lesson) {

        Lesson existing = findById(id);

        if (lesson.getTitle() != null && !lesson.getTitle().isBlank())
            existing.setTitle(lesson.getTitle());

        if (lesson.getOrderIndex() >= 0)
            existing.setOrderIndex(lesson.getOrderIndex());

        return lessonRepository.save(existing);
    }

    @Override
    public Lesson findById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new LessonNotFoundException("Lesson no encontrada"));
    }

    @Override
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        lessonRepository.delete(findById(id));
    }

    @Override
    public List<Lesson> findByCourse(Long courseId) {

        if (courseId == null)
            throw new LessonInvalidException("CourseId requerido");

        return lessonRepository.FindByCourseIdOrderByOrderIndex(courseId);
    }

    @Override
    public Lesson getFirstLesson(Long courseId) {

        List<Lesson> list = findByCourse(courseId);

        if (list.isEmpty())
            throw new LessonNotFoundException("Sin lecciones");

        return list.get(0);
    }

    @Override
    public Lesson getLastLesson(Long courseId) {

        List<Lesson> list = findByCourse(courseId);

        if (list.isEmpty())
            throw new LessonNotFoundException("Sin lecciones");

        return list.get(list.size() - 1);
    }

    @Override
    public void reorderLessons(Long courseId, List<Long> lessonIdsOrdered) {

        if (lessonIdsOrdered == null || lessonIdsOrdered.isEmpty())
            throw new InvalidLessonOrderException("Lista inválida");

        List<Lesson> lessons = findByCourse(courseId);

        if (lessons.size() != lessonIdsOrdered.size())
            throw new InvalidLessonOrderException("Cantidad inconsistente");

        for (int i = 0; i < lessonIdsOrdered.size(); i++) {
            Long lessonId = lessonIdsOrdered.get(i);

            Lesson lesson = lessons.stream()
                    .filter(l -> l.getId().equals(lessonId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidLessonOrderException("ID inválido"));

            lesson.setOrderIndex(i);
            lessonRepository.save(lesson);
        }
    }

    @Override
    public boolean existsByCourse(Long courseId) {
        return !findByCourse(courseId).isEmpty();
    }

    @Override
    public long countByCourse(Long courseId) {
        return findByCourse(courseId).size();
    }
}