package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
// Add other imports like lombok, persistence annotations as needed

@Data
@TableName("exercise_log") // Assuming your table name is exercise_log
public class ExerciseLog {

    @TableId(type = IdType.AUTO) // Assuming auto-increment ID
    private Long id;

    private Long userId; // Changed to use userId (Long)
    // private String username; // Removed username field in favor of userId

    private LocalDateTime exerciseTime;
    private String activity; // Renamed from exerciseName
    private Integer duration; // Assuming this is duration in minutes, as per new definition
    private Double caloriesBurned;
    private String notes;
    private LocalDateTime createdAt; // Renamed from logTime

    // Lombok @Data will generate getters, setters, constructor, toString, equals,
    // hashCode
}