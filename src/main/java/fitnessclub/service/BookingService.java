package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fitnessclub.model.Booking;
import fitnessclub.model.Lesson;
import fitnessclub.model.Member;
import fitnessclub.repository.BookingRepository;
import fitnessclub.repository.LessonRepository;
import fitnessclub.repository.MemberRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository repo;
    private final LessonRepository lessonRepo;
    private final MemberRepository memberRepo;

    public List<Booking> getAll() {
        return repo.findAll();
    }

    public Booking get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Booking not found: " + id));
    }

    @Transactional
    public Booking add(Booking booking) {
        if (booking.getMember() == null || booking.getMember().getId() == null
                || booking.getLesson() == null || booking.getLesson().getId() == null) {
            throw new IllegalArgumentException("Member and lesson are required");
        }
        // Участник и занятие берутся из БД, а не из тела запроса: иначе клиент мог бы прислать свою вместимость
        Member member = memberRepo.findById(booking.getMember().getId())
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + booking.getMember().getId()));
        Lesson lesson = lessonRepo.findById(booking.getLesson().getId())
                .orElseThrow(() -> new NoSuchElementException("Lesson not found: " + booking.getLesson().getId()));
        if (repo.countByLessonId(lesson.getId()) >= lesson.getCapacity()) {
            throw new IllegalStateException("Lesson is fully booked");
        }
        booking.setMember(member);
        booking.setLesson(lesson);
        return repo.save(booking);
    }

    public Booking update(Long id, Booking booking) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Booking not found: " + id);
        }
        booking.setId(id);
        return repo.save(booking);
    }

    public void cancel(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Booking not found: " + id);
        }
        repo.deleteById(id);
    }

    /** Бронь от имени участника разрешена пользователю только для своей учётной записи участника. */
    public boolean isUserMember(String username, Long memberId) {
        return memberId != null && memberRepo.findById(memberId)
                .map(member -> username.equals(member.getEmail()))
                .orElse(false);
    }

    public boolean isUserBooking(String username, Long bookingId) {
        return repo.findById(bookingId)
                .map(Booking::getMember)
                .map(member -> username.equals(member.getEmail()))
                .orElse(false);
    }
}
