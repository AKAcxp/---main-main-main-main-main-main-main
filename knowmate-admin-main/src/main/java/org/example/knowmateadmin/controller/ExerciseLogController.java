package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.entity.ExerciseLog;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.service.ExerciseLogService;
import org.example.knowmateadmin.service.UserService;
import org.example.knowmateadmin.utils.JwtUtil; // Assuming you have JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Added for Objects.equals comparison

@RestController
@RequestMapping("/api/exerciselog")
public class ExerciseLogController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseLogController.class);

    private final ExerciseLogService exerciseLogService;
    private final UserService userService; // Injected UserService
    // private final JwtUtil jwtUtil; // JwtUtil might not be needed if Principal is
    // consistently used

    @Autowired
    public ExerciseLogController(ExerciseLogService exerciseLogService, UserService userService) {
        this.exerciseLogService = exerciseLogService;
        this.userService = userService;
        // this.jwtUtil = jwtUtil;
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalStateException("用户未认证或无法从令牌中找到用户名");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            throw new IllegalStateException("根据用户名未能找到用户: " + principal.getName());
        }
        return user;
    }

    @PostMapping
    public ApiResponse<ExerciseLog> addExerciseLog(@RequestBody ExerciseLog exerciseLog, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        exerciseLog.setUserId(userId);

        if (exerciseLog.getExerciseTime() == null) {
            exerciseLog.setExerciseTime(LocalDateTime.now());
        }
        if (exerciseLog.getCreatedAt() == null) {
            exerciseLog.setCreatedAt(LocalDateTime.now());
        }

        boolean saved = exerciseLogService.save(exerciseLog);
        return saved ? ApiResponse.success(exerciseLog) : ApiResponse.error(400, "添加运动日志失败");
    }

    @GetMapping
    public ApiResponse<List<ExerciseLog>> getExerciseLogs(HttpServletRequest request) {
        try {
            logger.info("开始获取运动日志");
            Long userId = JwtUtil.getUserIdFromRequest(request);
            logger.info("用户ID: {}", userId);

            List<ExerciseLog> logs = exerciseLogService.getExerciseLogsByUserId(userId);
            logger.info("获取到 {} 条运动日志", logs != null ? logs.size() : 0);

            return ApiResponse.success(logs);
        } catch (Exception e) {
            logger.error("获取运动日志失败", e);
            return ApiResponse.error(500, "获取运动日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ExerciseLog> getExerciseLogById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        ExerciseLog log = exerciseLogService.getById(id);

        if (log != null && Objects.equals(log.getUserId(), userId)) {
            return ApiResponse.success(log);
        }
        return ApiResponse.error(404, "未找到运动日志或无权限访问");
    }

    @PutMapping("/{id}")
    public ApiResponse<ExerciseLog> updateExerciseLog(@PathVariable Long id,
            @RequestBody ExerciseLog exerciseLogDetails, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        ExerciseLog existingLog = exerciseLogService.getById(id);

        if (existingLog != null && Objects.equals(existingLog.getUserId(), userId)) {
            exerciseLogDetails.setId(id);
            exerciseLogDetails.setUserId(userId);

            if (exerciseLogDetails.getExerciseTime() == null) {
                exerciseLogDetails.setExerciseTime(existingLog.getExerciseTime());
            }
            if (exerciseLogDetails.getCreatedAt() == null) {
                exerciseLogDetails.setCreatedAt(existingLog.getCreatedAt());
            }

            boolean updated = exerciseLogService.updateById(exerciseLogDetails);
            return updated ? ApiResponse.success(exerciseLogDetails) : ApiResponse.error(400, "更新运动日志失败");
        }
        return ApiResponse.error(404, "未找到运动日志或无权限访问");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExerciseLog(@PathVariable Long id, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        ExerciseLog log = exerciseLogService.getById(id);

        if (log != null && Objects.equals(log.getUserId(), userId)) {
            boolean deleted = exerciseLogService.removeById(id);
            return deleted ? ApiResponse.success(null) : ApiResponse.error(400, "删除运动日志失败");
        }
        return ApiResponse.error(404, "未找到运动日志或无权限访问");
    }
}