
package fitnessclub.dto;

import fitnessclub.model.Member;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingFull {
    private Long id;
    @NotNull(message = "Member is required")
    private Member member;
    @NotNull(message = "Lesson is required")
    private LessonFull lesson;
    @NotNull(message = "Date is required")
    private LocalDate date;
}