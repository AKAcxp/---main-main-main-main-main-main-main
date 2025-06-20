package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
// Add other imports like lombok, persistence annotations as needed

@Data
@TableName("meal_log") // Assuming your table name is meal_log
public class MealLog {

    @TableId(type = IdType.AUTO) // Assuming auto-increment ID
    private Long id;

    private Long userId; // Changed from username to userId as per new definition
    // For consistency with existing controllers that use Principal.getName() (which
    // is String username)
    // we will keep username here for now. If you strictly want Long userId,
    // you'll need to adjust controllers to fetch User by username then get its Long
    // ID.
    // private String username;

    private LocalDateTime mealTime;
    private String foodName;
    private Double calories;
    private String notes;
    private LocalDateTime createdAt; // Renamed from logTime, and assuming this is auto-managed or set on creation

    // Lombok @Data will generate getters, setters, constructor, toString, equals,
    // hashCode
}