package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Booking;
import fitnessclub.repository.BookingRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repo;

    public List<Booking> getAllForMember(Long memberId) {
        return repo.findAll().stream()
                .filter(b -> b.getMember() != null && b.getMember().getId().equals(memberId))
                .toList();
    }

    public List<Booking> getAll() {
        return repo.findAll();
    }

    public Booking get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    public Booking add(Booking booking) {
        if (booking.getMember() == null || booking.getLesson() == null) {
            throw new IllegalArgumentException("Member and lesson are required");
        }
        // Check lesson capacity (example business rule)
        long bookingCount = repo.findAll().stream()
                .filter(b -> b.getLesson().getId().equals(booking.getLesson().getId()))
                .count();
        if (bookingCount >= booking.getLesson().getCapacity()) {
            throw new IllegalStateException("Lesson is fully booked");
        }
        return repo.save(booking);
    }

    public Booking update(Long id, Booking booking) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Booking not found: " + id);
        }
        booking.setId(id);
        return repo.save(booking);
    }

    public void cancel(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Booking not found: " + id);
        }
        repo.deleteById(id);
    }

    public boolean isUserBooking(String username, Long bookingId) {
        Booking booking = get(bookingId);
        if (booking == null || booking.getMember() == null) return false;
        return booking.getMember().getEmail().equals(username);
    }
}