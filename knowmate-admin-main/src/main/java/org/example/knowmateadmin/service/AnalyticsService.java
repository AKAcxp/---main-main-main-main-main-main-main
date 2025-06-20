package org.example.knowmateadmin.service;

import org.example.knowmateadmin.dto.BmiTrendDTO;
import org.example.knowmateadmin.dto.DailyCalorieSummaryDTO;
import org.example.knowmateadmin.dto.NutritionSummaryDTO;

public interface AnalyticsService {
    BmiTrendDTO getBmiTrendByUsername(String username);

    DailyCalorieSummaryDTO getDailyCalorieSummaryByUsername(String username);

    NutritionSummaryDTO getNutritionSummaryByUsername(String username);

    BmiTrendDTO getBmiTrendByUserId(Long userId);

    DailyCalorieSummaryDTO getDailyCalorieSummaryByUserId(Long userId);

    NutritionSummaryDTO getNutritionSummaryByUserId(Long userId);
}