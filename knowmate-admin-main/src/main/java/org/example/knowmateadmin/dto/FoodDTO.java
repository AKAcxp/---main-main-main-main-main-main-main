package org.example.knowmateadmin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodDTO {
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbohydrate;
    private String category;
    private String servingSize;
    private String flavor;
    private String reason;
    // 可以根据需要添加更多食物相关的字段，例如食物ID、分类、份量描述等
}