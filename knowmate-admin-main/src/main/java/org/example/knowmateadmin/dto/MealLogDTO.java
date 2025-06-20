package org.example.knowmateadmin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MealLogDTO {
    private String foodName;
    private Double calories;
    // Assuming mealTime will be sent as ISO string and parsed, or handled by
    // @JsonFormat if needed
    private LocalDateTime mealTime;
    // We might also need other fields like mealType (e.g., breakfast, lunch,
    // dinner) if relevant for DTO
}