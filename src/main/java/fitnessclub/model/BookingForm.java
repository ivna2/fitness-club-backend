package fitnessclub.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingForm {
    @NotNull(message = "ID участника обязателен")
    @Min(value = 1, message = "ID участника должен быть больше 0")
    private Long memberId;
    @NotNull(message = "ID занятия обязателен")
    @Min(value = 1, message = "ID занятия должен быть больше 0")
    private Long lessonId;
    @NotNull(message = "Дата обязательна")
    private LocalDate date;
}