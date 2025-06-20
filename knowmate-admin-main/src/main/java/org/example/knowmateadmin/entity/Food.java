package org.example.knowmateadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("food")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Double calories;
    private Double protein;
    private Double carbohydrate;
    private Double fat;
    private String category;
    private String servingSize;
    private String flavor;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
}