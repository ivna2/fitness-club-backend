package fitnessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fitnessclub.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {}
