package fitnessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fitnessclub.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {}

