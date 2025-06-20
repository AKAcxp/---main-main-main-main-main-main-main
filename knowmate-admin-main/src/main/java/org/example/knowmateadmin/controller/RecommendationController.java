package org.example.knowmateadmin.controller;

import lombok.RequiredArgsConstructor;
import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.RecommendationDTO;
import org.example.knowmateadmin.entity.Food;
import org.example.knowmateadmin.service.DietRecommendationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.knowmateadmin.service.UserService;
import org.example.knowmateadmin.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final DietRecommendationService dietRecommendationService;
    private final UserService userService;

    /**
     * 获取当前用户的饮食推荐
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<RecommendationDTO> getRecommendation(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return ApiResponse.error(404, "User not found.");
        }
        Long userId = currentUser.getId();
        RecommendationDTO recommendation = dietRecommendationService.generatePersonalizedRecommendation(userId);
        return ApiResponse.success(recommendation);
    }

    /**
     * 获取详细的饮食推荐信息
     */
    @GetMapping("/detailed")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<RecommendationDTO> getDetailedRecommendation(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        if (currentUser == null) {
            return ApiResponse.error(404, "User not found.");
        }
        Long userId = currentUser.getId();
        RecommendationDTO recommendation = dietRecommendationService.getDetailedDietRecommendationByUserId(userId);
        return ApiResponse.success(recommendation);
    }

    /**
     * 管理员获取指定用户的饮食推荐
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RecommendationDTO> getRecommendationByUserId(@PathVariable Long userId) {
        RecommendationDTO recommendation = dietRecommendationService.getDietRecommendationByUserId(userId);
        return ApiResponse.success(recommendation);
    }

    /**
     * 管理员获取指定用户的详细饮食推荐
     */
    @GetMapping("/user/{userId}/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RecommendationDTO> getDetailedRecommendationByUserId(@PathVariable Long userId) {
        RecommendationDTO recommendation = dietRecommendationService.getDetailedDietRecommendationByUserId(userId);
        return ApiResponse.success(recommendation);
    }
}