package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * User information entity class
 */
@Data
@TableName("user_info")
public class UserInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Double height;

    private Double weight;

    private Integer age;
    private String gender;

    private String goal;

    private String preference;

    @TableField("avoid_food")
    private String avoidFood;

    private LocalDateTime updatedAt;

    private Double bmi; // BMI ?
    private String bmiStatus; // BMI ????????????????
}