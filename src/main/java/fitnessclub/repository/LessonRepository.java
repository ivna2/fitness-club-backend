package fitnessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fitnessclub.model.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {}
