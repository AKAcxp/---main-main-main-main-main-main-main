package org.example.knowmateadmin.dto;

import lombok.Data;

/**
 * 用户信息数据传输对象
 */
@Data
public class UserInfoDTO {
    private Double height;
    private Double weight;
    private String goal;
    private String preference;
    private String avoidFood;
}