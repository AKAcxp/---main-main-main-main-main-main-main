package org.example.knowmateadmin.controller;

import lombok.RequiredArgsConstructor;
import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.BmiTrendDTO;
import org.example.knowmateadmin.dto.DailyCalorieSummaryDTO;
import org.example.knowmateadmin.dto.NutritionSummaryDTO;
import org.example.knowmateadmin.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/bmi-trend")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<BmiTrendDTO> getBmiTrend(Authentication authentication) {
        String username = authentication.getName();
        BmiTrendDTO bmiTrend = analyticsService.getBmiTrendByUsername(username);
        if (bmiTrend != null) {
            return ApiResponse.success("BMI趋势数据获取成功", bmiTrend);
        } else {
            return ApiResponse.error(404, "未找到BMI趋势数据");
        }
    }

    @GetMapping("/daily-calorie-summary")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<DailyCalorieSummaryDTO> getDailyCalorieSummary(Authentication authentication) {
        String username = authentication.getName();
        DailyCalorieSummaryDTO summary = analyticsService.getDailyCalorieSummaryByUsername(username);
        if (summary != null) {
            return ApiResponse.success("每日卡路里汇总数据获取成功", summary);
        } else {
            return ApiResponse.error(404, "未找到每日卡路里汇总数据");
        }
    }

    @GetMapping("/nutrition-summary")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<NutritionSummaryDTO> getNutritionSummary(Authentication authentication) {
        String username = authentication.getName();
        NutritionSummaryDTO summary = analyticsService.getNutritionSummaryByUsername(username);
        if (summary != null) {
            return ApiResponse.success("宏量营养素汇总数据获取成功", summary);
        } else {
            return ApiResponse.error(404, "未找到宏量营养素汇总数据");
        }
    }
}