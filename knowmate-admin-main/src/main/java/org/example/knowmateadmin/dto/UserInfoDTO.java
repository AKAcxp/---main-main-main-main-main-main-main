package org.example.knowmateadmin.dto;

import lombok.Data;

/**
 * User information data transfer object
 */
@Data
public class UserInfoDTO {
    private String username;
    private Integer age;
    private String gender;

    private Double height;
    private Double weight;
    private String goal;
    private String preference;
    private String avoidFood;

    // BMI related fields
    private Double bmi;
    private String bmiStatus;

    // toString method for logging
}