package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Subscription;
import fitnessclub.repository.SubscriptionRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repo;

    public List<Subscription> getAll() {
        return repo.findAll();
    }

    public Subscription get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Subscription not found: " + id));
    }

    public Subscription add(Subscription subscription) {
        return repo.save(subscription);
    }

    public Subscription update(Long id, Subscription subscription) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Subscription not found: " + id);
        }
        subscription.setId(id);
        return repo.save(subscription);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Subscription not found: " + id);
        }
        repo.deleteById(id);
    }
}
