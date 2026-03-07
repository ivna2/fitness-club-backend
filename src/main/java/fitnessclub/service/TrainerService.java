package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Trainer;
import fitnessclub.repository.TrainerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository repo;

    public List<Trainer> getAll() {
        return repo.findAll();
    }

    public Trainer get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Trainer add(Trainer trainer) {
        return repo.save(trainer);
    }

    public Trainer update(Long id, Trainer trainer) {
        trainer.setId(id);
        return repo.save(trainer);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
