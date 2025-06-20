package org.example.knowmateadmin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExerciseLogDTO {
    private String activity;
    private Integer duration; // Assuming duration is in minutes
    private Double caloriesBurned;
    private LocalDateTime exerciseTime;
}