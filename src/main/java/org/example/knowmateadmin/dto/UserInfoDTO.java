package org.example.knowmateadmin.dto;

import lombok.Data;

/**
 * �û���Ϣ���ݴ������
 */
@Data
public class UserInfoDTO {
    private Double height;
    private Double weight;
    private String goal;
    private String preference;
    private String avoidFood;
}