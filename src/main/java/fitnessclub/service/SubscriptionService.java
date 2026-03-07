package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Subscription;
import fitnessclub.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repo;

    public List<Subscription> getAllForMember(Long memberId) {
        return repo.findAll().stream()
                .filter(s -> s.getMember() != null && s.getMember().getId().equals(memberId))
                .toList();
    }

    public List<Subscription> getAll() {
        return repo.findAll();
    }

    public Subscription get(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Subscription add(Subscription subscription) {
        return repo.save(subscription);
    }

    public Subscription update(Long id, Subscription subscription) {
        subscription.setId(id);
        return repo.save(subscription);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
