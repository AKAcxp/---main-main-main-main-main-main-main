package org.example.knowmateadmin.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RecommendationDTO {
    private double bmr; // 基础代谢率
    private double targetCalories; // 目标卡路里
    private double currentNetCalories; // 当前净卡路里
    private double calorieBalance; // 卡路里差额
    private String recommendationText; // 推荐文本
    private List<FoodDTO> suggestedFoods; // 推荐食物列表
}