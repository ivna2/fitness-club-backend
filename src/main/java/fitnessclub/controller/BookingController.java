package fitnessclub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import fitnessclub.model.Booking;
import fitnessclub.service.BookingService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> get(@PathVariable Long id) {
        Booking booking = bookingService.get(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Booking> add(@Valid @RequestBody Booking booking) {
        return ResponseEntity.status(CREATED).body(bookingService.add(booking));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Booking> update(@PathVariable Long id, @Valid @RequestBody Booking booking) {
        Booking existing = bookingService.get(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookingService.update(id, booking));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingService.isUserBooking(authentication.name, #id)")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        Booking booking = bookingService.get(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        bookingService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}