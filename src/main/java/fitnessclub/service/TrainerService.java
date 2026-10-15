package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Trainer;
import fitnessclub.repository.TrainerRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository repo;

    public List<Trainer> getAll() {
        return repo.findAll();
    }

    public Trainer get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Trainer not found: " + id));
    }

    public Trainer add(Trainer trainer) {
        return repo.save(trainer);
    }

    public Trainer update(Long id, Trainer trainer) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Trainer not found: " + id);
        }
        trainer.setId(id);
        return repo.save(trainer);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Trainer not found: " + id);
        }
        repo.deleteById(id);
    }
}
