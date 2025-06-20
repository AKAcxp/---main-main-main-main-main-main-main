package org.example.knowmateadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.knowmateadmin.entity.MealLog;
import org.example.knowmateadmin.dto.MealLogDTO;

import java.time.LocalDate;
import java.util.List;

public interface MealLogService extends IService<MealLog> {
    List<MealLog> listByUser(Long userId);

    void add(MealLog log);

    void delete(Long id, Long userId);

    List<MealLog> getMealLogsByUsername(String username);

    List<MealLog> getMealLogsByUserId(Long userId);

    /**
     * 获取指定用户在指定日期的饮食日志。
     * 
     * @param userId 用户ID
     * @param date   日期
     * @return 饮食日志列表
     */
    List<MealLog> getMealLogsByUserIdAndDate(Long userId, LocalDate date);

    MealLog addMealLog(String username, MealLogDTO mealLogDTO);

    void deleteMealLog(Long mealLogId, String username);

    double getTodayTotalCaloriesConsumedByUsername(String username);

    /**
     * 获取指定用户当天消耗的总热量。
     * 
     * @param userId 用户ID
     * @return 当天消耗的总热量
     */
    double getTodayTotalCaloriesConsumedByUserId(Long userId);

    /**
     * 获取指定用户的所有饮食日志（分页）。
     * 
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页的饮食日志列表
     */
    Page<MealLog> getMealLogsByUserId(Long userId, int pageNum, int pageSize);
    // Add other specific service methods if needed
}