package org.example.knowmateadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.knowmateadmin.entity.ExerciseLog;
import org.example.knowmateadmin.dto.ExerciseLogDTO;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseLogService extends IService<ExerciseLog> {
    ExerciseLog addExerciseLog(String username, ExerciseLogDTO exerciseLogDTO);

    List<ExerciseLog> getExerciseLogsByUsername(String username);

    List<ExerciseLog> getExerciseLogsByUserId(Long userId);

    /**
     * 获取指定用户在指定日期的运动日志。
     * 
     * @param userId 用户ID
     * @param date   日期
     * @return 运动日志列表
     */
    List<ExerciseLog> getExerciseLogsByUserIdAndDate(Long userId, LocalDate date);

    void deleteExerciseLog(Long exerciseLogId, String username);

    double getTodayTotalCaloriesBurnedByUsername(String username);

    /**
     * 获取指定用户当天消耗的总运动热量。
     * 
     * @param userId 用户ID
     * @return 当天消耗的总运动热量
     */
    double getTodayTotalCaloriesBurnedByUserId(Long userId);
    // Add other specific service methods if needed
}