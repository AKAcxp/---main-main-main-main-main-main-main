package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息实体类
 */
@Data
@TableName("user_info")
public class UserInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private Double height;

    private Double weight;

    private String goal;

    private String preference;

    @TableField("avoid_food")
    private String avoidFood;

    private LocalDateTime updatedAt;

    private Double bmi; // BMI 值
    private String bmiStatus; // BMI 健康状态：偏瘦、正常、超重、肥胖
}