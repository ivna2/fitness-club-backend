package fitnessclub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fitnessclub.model.Lesson;
import fitnessclub.service.LessonService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ResponseEntity<List<Lesson>> getAll() {
        return ResponseEntity.ok(lessonService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> get(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.get(id));
    }

    @PostMapping
    public ResponseEntity<Lesson> add(@Valid @RequestBody Lesson lesson) {
        return ResponseEntity.status(CREATED).body(lessonService.add(lesson));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> update(@PathVariable Long id,
                                         @Valid @RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.update(id, lesson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
