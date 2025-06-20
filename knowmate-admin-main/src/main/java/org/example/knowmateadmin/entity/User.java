package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user") // 指定对应的数据库表名
public class User {

    /**
     * 用户 ID (主键，自增)
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名 (唯一)
     */
    private String username;

    private String email;

    /**
     * 密码 (加密存储)
     */
    private String password;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别 (例如: MALE, FEMALE, OTHER)
     */
    private String gender;

    /**
     * 身高 (cm)
     */
    private Double height;

    /**
     * 体重 (kg)
     */
    private Double weight;

    /**
     * BMI 值
     */
    private Double bmi;

    /**
     * BMI 状态 (例如: 偏瘦, 正常, 超重, 肥胖)
     */
    private String bmiStatus;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 注册时间
     */
    private LocalDateTime createTime; // 使用 LocalDateTime 对应 DATETIME 类型，MyBatis Plus 会自动处理驼峰命名和下划线的转换

    private LocalDateTime UpdateTime;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 账户是否启用
     */
    private Boolean isEnabled;
}