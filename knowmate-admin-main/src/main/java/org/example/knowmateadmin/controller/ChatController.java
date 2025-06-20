package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.dto.BmiTrendDTO;
import org.example.knowmateadmin.dto.DailyCalorieSummaryDTO;
import org.example.knowmateadmin.dto.NutritionSummaryDTO;
import org.example.knowmateadmin.dto.RecommendationDTO;
import org.example.knowmateadmin.entity.ExerciseLog;
import org.example.knowmateadmin.entity.MealLog;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.knowmateadmin.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    @Autowired
    private GoogleAiService aiService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private MealLogService mealLogService;

    @Autowired
    private ExerciseLogService exerciseLogService;

    @Autowired
    private DietRecommendationService dietRecommendationService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String prompt = body.get("prompt");
        String frontendContext = body.get("context");

        System.out.println("Received prompt: " + prompt);
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "内容不能为空"));
        }

        Long userId = JwtUtil.extractUserId(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未认证或无法获取用户ID"));
        }

        StringBuilder aiResponseBuilder = new StringBuilder();

        try {
            String lowerCasePrompt = prompt.toLowerCase();
            System.out.println("Lowercase prompt: " + lowerCasePrompt);

            UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
            DailyCalorieSummaryDTO dailyCalorieSummary = analyticsService.getDailyCalorieSummaryByUserId(userId);
            List<MealLog> mealLogsToday = mealLogService.getMealLogsByUserIdAndDate(userId, LocalDate.now());
            List<ExerciseLog> exerciseLogsToday = exerciseLogService.getExerciseLogsByUserIdAndDate(userId,
                    LocalDate.now());

            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("以下是关于用户的信息，用于辅助回答：\n");
            if (userInfo != null) {
                contextBuilder.append(String.format(
                        "用户档案：年龄 %d 岁, 性别 %s, 身高 %.1f cm, 体重 %.1f kg, 健身目标 %s, 偏好口味 %s, 饮食禁忌 %s。",
                        userInfo.getAge(),
                        userInfo.getGender() != null ? userInfo.getGender().toString() : "未知",
                        userInfo.getHeight(),
                        userInfo.getWeight(),
                        userInfo.getGoal() != null ? userInfo.getGoal().toString() : "未知",
                        userInfo.getPreference() != null && !userInfo.getPreference().isEmpty()
                                ? userInfo.getPreference()
                                : "无",
                        userInfo.getAvoidFood() != null && !userInfo.getAvoidFood().isEmpty() ? userInfo.getAvoidFood()
                                : "无"))
                        .append("\n");
            }
            if (dailyCalorieSummary != null) {
                Double todayConsumed = dailyCalorieSummary.getConsumedCalories() != null
                        && !dailyCalorieSummary.getConsumedCalories().isEmpty()
                                ? dailyCalorieSummary.getConsumedCalories()
                                        .get(dailyCalorieSummary.getConsumedCalories().size() - 1)
                                : 0.0;
                Double todayBurned = dailyCalorieSummary.getBurnedCalories() != null
                        && !dailyCalorieSummary.getBurnedCalories().isEmpty()
                                ? dailyCalorieSummary.getBurnedCalories()
                                        .get(dailyCalorieSummary.getBurnedCalories().size() - 1)
                                : 0.0;
                contextBuilder.append(String.format("今日热量摄入: %.0f 大卡, 今日热量消耗: %.0f 大卡。", todayConsumed, todayBurned))
                        .append("\n");
            }
            if (mealLogsToday != null && !mealLogsToday.isEmpty()) {
                contextBuilder.append("今日饮食记录：");
                mealLogsToday.forEach(log -> contextBuilder
                        .append(String.format("%s (%.0fkcal); ", log.getFoodName(), log.getCalories())));
                contextBuilder.append("\n");
            }
            if (exerciseLogsToday != null && !exerciseLogsToday.isEmpty()) {
                contextBuilder.append("今日运动记录：");
                exerciseLogsToday.forEach(log -> contextBuilder
                        .append(String.format("%s (%.0fkcal); ", log.getActivity(), log.getCaloriesBurned())));
                contextBuilder.append("\n");
            }

            String fullContext = contextBuilder.toString();
            if (frontendContext != null && !frontendContext.trim().isEmpty()) {
                fullContext = "前端上下文：" + frontendContext + "\n" + fullContext;
            }
            System.out.println("Full Context for AI: " + fullContext);

            if (lowerCasePrompt.contains("热量") || lowerCasePrompt.contains("卡路里") || lowerCasePrompt.contains("摄入多少")) {
                System.out.println("Entering Daily Calorie Summary branch for 'how many calories today'.");
                if (dailyCalorieSummary != null) {
                    Double todayConsumed = dailyCalorieSummary.getConsumedCalories() != null
                            && !dailyCalorieSummary.getConsumedCalories().isEmpty()
                                    ? dailyCalorieSummary.getConsumedCalories()
                                            .get(dailyCalorieSummary.getConsumedCalories().size() - 1)
                                    : 0.0;
                    Double todayBurned = dailyCalorieSummary.getBurnedCalories() != null
                            && !dailyCalorieSummary.getBurnedCalories().isEmpty()
                                    ? dailyCalorieSummary.getBurnedCalories()
                                            .get(dailyCalorieSummary.getBurnedCalories().size() - 1)
                                    : 0.0;
                    aiResponseBuilder
                            .append(String.format("你今天摄入的总热量约为 %.0f 大卡，消耗热量约为 %.0f 大卡。", todayConsumed, todayBurned));
                } else {
                    aiResponseBuilder.append("暂无今日热量摄入与消耗数据，请先记录饮食和运动日志。");
                }
            } else if (lowerCasePrompt.contains("bmi") || lowerCasePrompt.contains("体重指数")
                    || lowerCasePrompt.contains("体重变化")) {
                System.out.println("Entering BMI trend branch.");
                BmiTrendDTO bmiTrend = analyticsService.getBmiTrendByUserId(userId);
                if (bmiTrend != null && bmiTrend.getDates() != null && !bmiTrend.getDates().isEmpty()
                        && bmiTrend.getBmiValues() != null && !bmiTrend.getBmiValues().isEmpty()) {
                    String latestDate = bmiTrend.getDates().get(bmiTrend.getDates().size() - 1);
                    Double latestBmi = bmiTrend.getBmiValues().get(bmiTrend.getBmiValues().size() - 1);

                    if (latestBmi != null) {
                        aiResponseBuilder.append(String.format("根据你最近的记录（%s），你的BMI是 %.2f。\n", latestDate, latestBmi));
                        if (latestBmi < 18.5) {
                            aiResponseBuilder.append("这表示你可能体重过轻，建议增加健康饮食和适量运动来增重。");
                        } else if (latestBmi >= 18.5 && latestBmi < 24) {
                            aiResponseBuilder.append("你的BMI在正常范围内，请继续保持健康的饮食和生活习惯！");
                        } else if (latestBmi >= 24 && latestBmi < 28) {
                            aiResponseBuilder.append("这表示你可能超重了，建议调整饮食结构，增加运动量。");
                        } else {
                            aiResponseBuilder.append("这表示你可能已经肥胖，为了您的健康，建议咨询医生或营养师，并制定详细的减重计划。");
                        }
                    } else {
                        aiResponseBuilder.append("暂无有效的BMI数据，请确保您的健康档案完整。");
                    }
                } else {
                    aiResponseBuilder.append("暂无BMI变化数据，请先完善健康档案。");
                }
            } else if (lowerCasePrompt.contains("推荐") || lowerCasePrompt.contains("食谱")
                    || lowerCasePrompt.contains("吃什么")) {
                System.out.println("Entering Recommendation branch.");
                RecommendationDTO recommendationDTO = dietRecommendationService
                        .generatePersonalizedRecommendation(userId);
                if (recommendationDTO != null && recommendationDTO.getSuggestedFoods() != null
                        && !recommendationDTO.getSuggestedFoods().isEmpty()) {
                    aiResponseBuilder.append("根据您的健康目标和今日情况，我的智能饮食推荐如下：\n\n");
                    aiResponseBuilder.append("**推荐理由：** ").append(recommendationDTO.getRecommendationText())
                            .append("\n\n");
                    aiResponseBuilder.append("**推荐食物列表：**\n");
                    recommendationDTO.getSuggestedFoods().forEach(food -> aiResponseBuilder
                            .append(String.format("- %s (热量: %.0fkcal, 蛋白质: %.1fg, 脂肪: %.1fg, 碳水化合物: %.1fg, 理由: %s)\n",
                                    food.getName(),
                                    food.getCalories(),
                                    food.getProtein(),
                                    food.getFat(),
                                    food.getCarbohydrate(),
                                    food.getReason())));
                } else {
                    aiResponseBuilder.append("暂无法生成饮食推荐，请确保您的健康档案完整并记录了当日饮食和运动日志。");
                }
            } else if (lowerCasePrompt.contains("今天吃了什么") || lowerCasePrompt.contains("饮食日志")
                    || lowerCasePrompt.contains("餐食记录")) {
                System.out.println("Entering individual Meal Log branch.");
                if (mealLogsToday != null && !mealLogsToday.isEmpty()) {
                    aiResponseBuilder.append("你今天记录的饮食日志如下：\n");
                    mealLogsToday.forEach(log -> aiResponseBuilder.append(String.format("- %s (热量: %.0f大卡), 时间: %s\n",
                            log.getFoodName(),
                            log.getCalories(),
                            log.getMealTime().toLocalTime().toString())));
                } else {
                    aiResponseBuilder.append("你今天还没有记录任何饮食日志。");
                }
            } else if (lowerCasePrompt.contains("今天运动了多少") || lowerCasePrompt.contains("运动日志")
                    || lowerCasePrompt.contains("运动记录")) {
                System.out.println("Entering individual Exercise Log branch.");
                if (exerciseLogsToday != null && !exerciseLogsToday.isEmpty()) {
                    aiResponseBuilder.append("你今天记录的运动日志如下：\n");
                    exerciseLogsToday.forEach(
                            log -> aiResponseBuilder.append(String.format("- %s (时长: %d分钟, 消耗热量: %.0f大卡), 时间: %s\n",
                                    log.getActivity(),
                                    log.getDuration(),
                                    log.getCaloriesBurned(),
                                    log.getExerciseTime().toLocalTime().toString())));
                } else {
                    aiResponseBuilder.append("你今天还没有记录任何运动日志。");
                }
            } else {
                System.out.println("Entering general AI service branch.");
                String promptWithContext = fullContext + "用户问题：" + prompt;
                String aiRawResponse = aiService.chat(promptWithContext);
                aiResponseBuilder.append(aiRawResponse);
            }

            return ResponseEntity.ok(Map.of("answer", aiResponseBuilder.toString()));

        } catch (Exception e) {
            System.err.println("Error in AI chat processing: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "与AI助手通信时发生错误，请稍后再试。"));
        }
    }
}