package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.knowmateadmin.dto.BmiTrendDTO;
import org.example.knowmateadmin.dto.DailyCalorieSummaryDTO;
import org.example.knowmateadmin.dto.NutritionSummaryDTO;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.MealLogMapper;
import org.example.knowmateadmin.mapper.ExerciseLogMapper;
import org.example.knowmateadmin.mapper.UserInfoMapper;
import org.example.knowmateadmin.service.AnalyticsService;
import org.example.knowmateadmin.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserService userService;
    private final UserInfoMapper userInfoMapper;
    private final MealLogMapper mealLogMapper;
    private final ExerciseLogMapper exerciseLogMapper;

    @Override
    public BmiTrendDTO getBmiTrendByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("AnalyticsService: User not found for username: {}", username);
            return null;
        }
        return getBmiTrendByUserId(user.getId());
    }

    @Override
    public BmiTrendDTO getBmiTrendByUserId(Long userId) {
        // 获取用户所有健康档案，并按更新时间降序排列
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("updated_at");
        List<UserInfo> userInfoList = userInfoMapper.selectList(queryWrapper);

        if (userInfoList == null || userInfoList.isEmpty()) {
            log.info("AnalyticsService: No user info found for userId: {}", userId);
            return BmiTrendDTO.builder()
                    .dates(new ArrayList<>())
                    .bmiValues(new ArrayList<>())
                    .build();
        }

        // 使用 TreeMap 存储每日最新的 BMI，确保日期有序且唯一
        Map<LocalDate, Double> dailyBmiMap = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (UserInfo info : userInfoList) {
            if (info.getUpdatedAt() != null) {
                LocalDate date = info.getUpdatedAt().toLocalDate();
                // 如果当天还没有记录，或者新记录的 BMI 字段非空（优先使用计算好的 BMI）
                // 确保我们只记录当天最新或最完整的 BMI 数据
                if (!dailyBmiMap.containsKey(date) || (info.getBmi() != null && dailyBmiMap.get(date) == null)) {
                    Double bmi = info.getBmi();
                    if (bmi == null && info.getHeight() != null && info.getWeight() != null && info.getHeight() > 0
                            && info.getWeight() > 0) {
                        double heightInMeters = info.getHeight() / 100.0;
                        bmi = BigDecimal.valueOf(info.getWeight() / (heightInMeters * heightInMeters))
                                .setScale(1, RoundingMode.HALF_UP)
                                .doubleValue();
                    }
                    if (bmi != null) {
                        dailyBmiMap.put(date, bmi);
                    }
                }
            }
        }

        // 获取最近 7 天的日期
        List<String> dates = new ArrayList<>();
        List<Double> bmiValues = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) { // 从过去第6天到今天
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            bmiValues.add(dailyBmiMap.getOrDefault(date, null)); // 如果当天没有数据，则为null
        }

        return BmiTrendDTO.builder()
                .dates(dates)
                .bmiValues(bmiValues)
                .build();
    }

    @Override
    public DailyCalorieSummaryDTO getDailyCalorieSummaryByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("AnalyticsService: User not found for username: {}", username);
            return null;
        }
        return getDailyCalorieSummaryByUserId(user.getId());
    }

    @Override
    public DailyCalorieSummaryDTO getDailyCalorieSummaryByUserId(Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dates = new ArrayList<>();
        List<Double> consumedCalories = new ArrayList<>();
        List<Double> burnedCalories = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) { // 从过去第6天到今天
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            Double dailyConsumed = mealLogMapper.sumCaloriesByUserIdAndDateRange(userId, startOfDay, endOfDay);
            Double dailyBurned = exerciseLogMapper.sumCaloriesBurnedForDateRange(userId, startOfDay, endOfDay);

            consumedCalories.add(dailyConsumed != null ? dailyConsumed : 0.0);
            burnedCalories.add(dailyBurned != null ? dailyBurned : 0.0);
        }

        return DailyCalorieSummaryDTO.builder()
                .dates(dates)
                .consumedCalories(consumedCalories)
                .burnedCalories(burnedCalories)
                .build();
    }

    @Override
    public NutritionSummaryDTO getNutritionSummaryByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("AnalyticsService: User not found for username: {}", username);
            return null;
        }
        return getNutritionSummaryByUserId(user.getId());
    }

    @Override
    public NutritionSummaryDTO getNutritionSummaryByUserId(Long userId) {
        // 获取最近7天
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(7);

        Map<String, Double> nutritionData = mealLogMapper.sumNutritionByUserIdAndDateRange(userId, startTime, endTime);

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        // 添加蛋白质标签
        labels.add("蛋白质");
        values.add(nutritionData.getOrDefault("total_protein", 0.0));

        // 添加碳水化合物标签
        labels.add("碳水化合物");
        values.add(nutritionData.getOrDefault("total_carbohydrate", 0.0));

        // 添加脂肪标签
        labels.add("脂肪");
        values.add(nutritionData.getOrDefault("total_fat", 0.0));

        return NutritionSummaryDTO.builder()
                .labels(labels)
                .values(values)
                .build();
    }
}