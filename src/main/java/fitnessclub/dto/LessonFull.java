package fitnessclub.dto;

import fitnessclub.model.Trainer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LessonFull {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Trainer is required")
    private Trainer trainer;
    @NotNull(message = "Date and time are required")
    private LocalDateTime dateTime;
    @Positive(message = "Capacity must be positive")
    private int capacity;
}
