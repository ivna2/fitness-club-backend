package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Lesson;
import fitnessclub.repository.LessonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository repo;

    public List<Lesson> getAll() {
        return repo.findAll();
    }

    public Lesson get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Lesson add(Lesson lesson) {
        return repo.save(lesson);
    }

    public Lesson update(Long id, Lesson lesson) {
        lesson.setId(id);
        return repo.save(lesson);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
