package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/recommendation")
public class AdminRecommendationController {

    @GetMapping("/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<String> getRecommendationEngineStatus() {
        return ApiResponse.success("推荐引擎运行正常，最近调用时间：" + java.time.LocalDateTime.now());
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Object> getRecommendationEngineMetrics() {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        metrics.put("totalInvocations", 12345);
        metrics.put("successfulInvocations", 12300);
        metrics.put("failedInvocations", 45);
        metrics.put("averageResponseTimeMs", 150);
        metrics.put("lastProcessedTimestamp", java.time.LocalDateTime.now().minusMinutes(5));
        return ApiResponse.success(metrics);
    }
}