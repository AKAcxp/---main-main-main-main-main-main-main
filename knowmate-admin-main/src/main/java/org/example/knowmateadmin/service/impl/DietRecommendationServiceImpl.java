package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.knowmateadmin.dto.RecommendationDTO;
import org.example.knowmateadmin.dto.FoodDTO;
import org.example.knowmateadmin.entity.Food;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.mapper.FoodMapper;
import org.example.knowmateadmin.mapper.MealLogMapper;
import org.example.knowmateadmin.mapper.ExerciseLogMapper;
import org.example.knowmateadmin.service.DietRecommendationService;
import org.example.knowmateadmin.service.UserInfoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietRecommendationServiceImpl implements DietRecommendationService {

    private final UserInfoService userInfoService;
    private final FoodMapper foodMapper;
    private final MealLogMapper mealLogMapper;
    private final ExerciseLogMapper exerciseLogMapper;

    @Override
    public RecommendationDTO getRecommendation(Long userId) {
        return getDetailedDietRecommendationByUserId(userId);
    }

    @Override
    public RecommendationDTO getDietRecommendationByUserId(Long userId) {
        return getDetailedDietRecommendationByUserId(userId);
    }

    @Override
    public RecommendationDTO getDetailedDietRecommendationByUserId(Long userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null) {
            log.warn("未找到用户信息: userId = {}", userId);
            return null;
        }

        // 获取今日已摄入的卡路里和运动消耗的卡路里
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Double todayCalories = mealLogMapper.sumCaloriesByUserIdAndDateRange(userInfo.getUserId(), startOfDay,
                endOfDay);
        Double todayBurnedCalories = exerciseLogMapper.sumCaloriesBurnedForDateRange(userInfo.getUserId(), startOfDay,
                endOfDay);

        // 处理 null 值
        todayCalories = todayCalories != null ? todayCalories : 0.0;
        todayBurnedCalories = todayBurnedCalories != null ? todayBurnedCalories : 0.0;

        return generateRecommendation(userInfo, todayCalories, todayBurnedCalories);
    }

    @Override
    public RecommendationDTO generatePersonalizedRecommendation(Long userId) {
        // Step 1: 获取用户信息（BMI、目标等）
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null) {
            log.warn("生成个性化推荐：未找到用户信息: userId = {}", userId);
            return null;
        }

        // Step 2: 统计当天饮食摄入热量
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Double totalIntake = mealLogMapper.sumCaloriesByUserIdAndDateRange(userId, startOfDay, endOfDay);
        totalIntake = totalIntake != null ? totalIntake : 0.0; // 处理 null 值

        // Step 3: 统计当天运动消耗热量
        Double totalBurned = exerciseLogMapper.sumCaloriesBurnedForDateRange(userId, startOfDay, endOfDay);
        totalBurned = totalBurned != null ? totalBurned : 0.0; // 处理 null 值

        // Step 4: 计算热量缺口 / 盈余
        // 注意：这里的 calorieGap 实际上是 "净摄入热量"：摄入 - 消耗
        // generateRecommendation 方法会根据目标卡路里和这个净摄入热量来计算真正的"缺口/盈余"
        return generateRecommendation(userInfo, totalIntake, totalBurned);
    }

    private RecommendationDTO generateRecommendation(UserInfo userInfo, Double todayCalories,
            Double todayBurnedCalories) {
        // 1. 计算基础代谢率 (BMR)
        double bmr = calculateBMR(userInfo);

        // 2. 计算目标卡路里
        double targetCalories = calculateTargetCalories(bmr, userInfo.getGoal());

        // 3. 计算卡路里差额
        double netCalories = todayCalories - todayBurnedCalories;
        double calorieBalance = targetCalories - netCalories; // 正值表示实际摄入低于目标（缺口），负值表示实际摄入高于目标（盈余）

        // 4. 根据用户目标和偏好精细化推荐食物
        List<Food> recommendedFoods = new ArrayList<>();
        String recommendationText = "";

        try {
            if ("muscle_gain".equals(userInfo.getGoal())) {
                // 增肌，需要热量盈余。
                // calorieBalance > 0 表示缺口（实际摄入低于目标），calorieBalance越大，缺口越大。
                // calorieBalance < 0 表示盈余（实际摄入高于目标），calorieBalance绝对值越大，盈余越大。
                if (calorieBalance > 300) { // 热量缺口过大 (目标比实际高300+)
                    recommendedFoods = foodMapper.selectHighProteinFoods(userInfo.getPreference(), 3);
                    recommendationText = String.format("您今天热量缺口较大，还需要摄入约 %.0f 卡路里以达到增肌目标。建议多补充高蛋白食物。", calorieBalance);
                } else if (calorieBalance < -300) { // 热量盈余过大 (实际比目标高300+)
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3); // 此时应推荐均衡，避免过度摄入
                    recommendationText = String.format("您今天摄入热量已超出增肌目标 %.0f 卡路里。建议保持均衡饮食。", Math.abs(calorieBalance));
                } else { // 热量接近目标
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3);
                    recommendationText = "您的卡路里摄入接近增肌目标，请继续保持均衡饮食。";
                }
            } else if ("fat_loss".equals(userInfo.getGoal())) {
                // 减脂，需要热量缺口。
                // calorieBalance > 0 表示缺口（实际摄入低于目标），calorieBalance越大，缺口越大。
                // calorieBalance < 0 表示盈余（实际摄入高于目标），calorieBalance绝对值越大，盈余越大。
                if (calorieBalance < -300) { // 热量盈余过大 (实际比目标高300+)，阻碍减脂
                    recommendedFoods = foodMapper.selectLowCalorieLowFatFoods(userInfo.getPreference(), 3);
                    recommendationText = String.format("您今天摄入热量已超出减脂目标 %.0f 卡路里。建议选择低热量、高纤维食物以助减脂。",
                            Math.abs(calorieBalance));
                } else if (calorieBalance > 300) { // 热量缺口过大 (目标比实际高300+)，可能导致营养不足
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3); // 此时应推荐均衡，避免过度节食
                    recommendationText = String.format("您今天热量缺口较大，建议补充适量均衡食物，避免过度节食。");
                } else { // 热量接近目标或已在合理缺口内
                    recommendedFoods = foodMapper.selectLowCalorieLowFatFoods(userInfo.getPreference(), 3);
                    recommendationText = "您的卡路里摄入接近减脂目标，请继续保持低卡低脂饮食。";
                }
            } else { // 保持健康或其他目标
                // 这里仍使用目标与净摄入的差值来判断是否需要调整
                if (calorieBalance > 200) { // 热量缺口较大（实际摄入低于目标200+）
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3);
                    recommendationText = String.format("您今天还需要摄入约 %.0f 卡路里，建议补充均衡食物以维持健康。", calorieBalance);
                } else if (calorieBalance < -200) { // 热量盈余较大（实际摄入高于目标200+）
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3);
                    recommendationText = String.format("您今天已摄入 %.0f 卡路里，已超出推荐摄入量。建议保持均衡饮食。", Math.abs(calorieBalance));
                } else { // 热量接近目标
                    recommendedFoods = foodMapper.selectBalancedFoods(userInfo.getPreference(), 3);
                    recommendationText = "您的卡路里摄入接近健康维持目标，请继续保持均衡饮食。";
                }
            }
        } catch (Exception e) {
            log.error("查询推荐食物时发生错误", e);
            recommendationText = "今天没有特别的饮食建议，请保持均衡饮食！";
        }

        // 6. 转换 Food 实体到 FoodDTO
        List<FoodDTO> foodDTOs = recommendedFoods.stream()
                .map(food -> {
                    String foodReason = generateFoodReason(food); // 生成推荐理由
                    return FoodDTO.builder()
                            .id(food.getId())
                            .name(food.getName())
                            .calories(food.getCalories())
                            .protein(food.getProtein() != null ? food.getProtein() : 0.0)
                            .fat(food.getFat() != null ? food.getFat() : 0.0)
                            .carbohydrate(food.getCarbohydrate() != null ? food.getCarbohydrate() : 0.0) // 使用getCarbohydrate()
                            .category(food.getCategory())
                            .servingSize(food.getServingSize())
                            .flavor(food.getFlavor())
                            .reason(foodReason) // 设置推荐理由
                            .build();
                })
                .collect(Collectors.toList());

        // 7. 构建并返回推荐 DTO
        return RecommendationDTO.builder()
                .bmr(bmr)
                .targetCalories(targetCalories)
                .currentNetCalories(netCalories)
                .calorieBalance(calorieBalance)
                .recommendationText(recommendationText)
                .suggestedFoods(foodDTOs)
                .build();
    }

    private double calculateBMR(UserInfo userInfo) {
        // 使用 Mifflin-St Jeor 公式
        // 男性：BMR = 10 × 体重(kg) + 6.25 × 身高(cm) - 5 × 年龄 + 5
        // 女性：BMR = 10 × 体重(kg) + 6.25 × 身高(cm) - 5 × 年龄 - 161
        // 确保身高和体重不为 null
        double height = userInfo.getHeight() != null ? userInfo.getHeight() : 0;
        double weight = userInfo.getWeight() != null ? userInfo.getWeight() : 0;
        int age = userInfo.getAge() != null ? userInfo.getAge() : 25; // 默认年龄25
        String gender = userInfo.getGender() != null ? userInfo.getGender() : "MALE"; // 默认男性

        double bmr;
        if ("FEMALE".equalsIgnoreCase(gender)) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        }
        return bmr;
    }

    private double calculateTargetCalories(double bmr, String goal) {
        // 根据目标调整卡路里
        // 假设活动水平为中等 (1.375)
        double activityFactor = 1.375;
        double tdee = bmr * activityFactor;

        if (goal == null) {
            return tdee;
        }

        switch (goal) {
            case "muscle_gain":
                return tdee + 300; // 增肌需要热量盈余
            case "fat_loss":
                return tdee - 500; // 减脂需要热量缺口
            default:
                return tdee; // 保持体重或塑形
        }
    }

    private String generateFoodReason(Food food) {
        StringBuilder reasonBuilder = new StringBuilder();
        // 根据食物的营养成分生成推荐理由
        if (food.getProtein() != null && food.getProtein() > 20) {
            reasonBuilder.append("高蛋白，");
        }
        if (food.getFat() != null && food.getFat() < 10) {
            reasonBuilder.append("低脂肪，");
        }
        if (food.getCalories() != null && food.getCalories() < 150) {
            reasonBuilder.append("低卡路里，");
        }
        if (food.getCarbohydrate() != null && food.getCarbohydrate() > 30) { // Changed to getCarbohydrate()
            reasonBuilder.append("富含碳水化合物，");
        }
        if (food.getCategory() != null && !food.getCategory().isEmpty()) {
            reasonBuilder.append(food.getCategory()).append("，");
        }
        if (food.getFlavor() != null && !food.getFlavor().isEmpty()) {
            reasonBuilder.append(food.getFlavor()).append("口味，");
        }

        // 移除末尾的逗号（如果存在）
        if (reasonBuilder.length() > 0 && reasonBuilder.charAt(reasonBuilder.length() - 1) == '，') {
            reasonBuilder.setLength(reasonBuilder.length() - 1);
        }
        return reasonBuilder.toString().isEmpty() ? "营养均衡" : reasonBuilder.toString();
    }
}