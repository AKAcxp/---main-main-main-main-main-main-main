package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.MealLogDTO;
import org.example.knowmateadmin.entity.MealLog;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.service.MealLogService;
import org.example.knowmateadmin.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meal-log")
@RequiredArgsConstructor
@Slf4j
public class MealLogController {

    private final MealLogService mealLogService;
    private final UserService userService;

    /**
     * 获取当前用户的饮食日志
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<Page<MealLog>> getMealLogs(@RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        Page<MealLog> page = mealLogService.getMealLogsByUserId(userId, pageNum, pageSize);
        return ApiResponse.success(page);
    }

    /**
     * 添加饮食日志
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<Void> addMealLog(@RequestBody MealLog mealLog) {
        mealLog.setUserId(getCurrentUserId());
        mealLogService.save(mealLog);
        return ApiResponse.success("饮食记录添加成功");
    }

    /**
     * 删除饮食日志
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<Void> deleteMealLog(@PathVariable Long id) {
        mealLogService.removeById(id);
        return ApiResponse.success("饮食记录删除成功");
    }

    /**
     * 获取当前用户今日总卡路里摄入
     */
    @GetMapping("/today/calories")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ApiResponse<Double> getTodayTotalCalories(Authentication authentication) {
        String username = authentication.getName();
        double totalCalories = mealLogService.getTodayTotalCaloriesConsumedByUsername(username);
        return ApiResponse.success(totalCalories);
    }

    /**
     * 获取指定用户的饮食日志（管理员权限）
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<Page<MealLog>> getAllMealLogs(@RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<MealLog> page = mealLogService.page(new Page<>(pageNum, pageSize));
        return ApiResponse.success(page);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("Attempted to get userId for non-existent user: {}", username);
            throw new RuntimeException("User not found or not authenticated properly.");
        }
        return user.getId();
    }
}