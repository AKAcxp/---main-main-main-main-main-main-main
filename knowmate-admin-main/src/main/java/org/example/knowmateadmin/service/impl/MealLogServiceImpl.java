package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.knowmateadmin.dto.MealLogDTO;
import org.example.knowmateadmin.entity.MealLog;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.MealLogMapper;
import org.example.knowmateadmin.service.MealLogService;
import org.example.knowmateadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
public class MealLogServiceImpl extends ServiceImpl<MealLogMapper, MealLog> implements MealLogService {

    private final UserService userService;

    @Autowired
    public MealLogServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<MealLog> listByUser(Long userId) {
        return list(new QueryWrapper<MealLog>().eq("user_id", userId).orderByDesc("meal_time"));
    }

    @Override
    @Transactional
    public void add(MealLog log) {
        User user = userService.getById(log.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found for ID: " + log.getUserId());
        }
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        if (log.getMealTime() == null) {
            log.setMealTime(LocalDateTime.now());
        }
        save(log);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        remove(new QueryWrapper<MealLog>().eq("id", id).eq("user_id", userId));
    }

    @Override
    @Transactional
    public MealLog addMealLog(String username, MealLogDTO mealLogDTO) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        MealLog mealLog = new MealLog();
        mealLog.setUserId(user.getId());
        mealLog.setFoodName(mealLogDTO.getFoodName());
        mealLog.setCalories(mealLogDTO.getCalories());
        mealLog.setMealTime(mealLogDTO.getMealTime() != null ? mealLogDTO.getMealTime() : LocalDateTime.now());
        mealLog.setCreatedAt(LocalDateTime.now());
        save(mealLog);
        return mealLog;
    }

    @Override
    public List<MealLog> getMealLogsByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return getMealLogsByUserId(user.getId());
    }

    @Override
    public List<MealLog> getMealLogsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(new QueryWrapper<MealLog>().eq("user_id", userId).orderByDesc("meal_time"));
    }

    @Override
    public List<MealLog> getMealLogsByUserIdAndDate(Long userId, LocalDate date) {
        if (userId == null || date == null) {
            return Collections.emptyList();
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return list(new QueryWrapper<MealLog>()
                .eq("user_id", userId)
                .between("meal_time", startOfDay, endOfDay)
                .orderByAsc("meal_time"));
    }

    @Override
    @Transactional
    public void deleteMealLog(Long mealLogId, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        remove(new QueryWrapper<MealLog>().eq("id", mealLogId).eq("user_id", user.getId()));
    }

    @Override
    public double getTodayTotalCaloriesConsumedByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            // log.warn("User not found for calorie calculation: " + username);
            return 0.0;
        }
        return getTodayTotalCaloriesConsumedByUserId(user.getId());
    }

    @Override
    public double getTodayTotalCaloriesConsumedByUserId(Long userId) {
        if (userId == null) {
            // log.warn("User ID is null, cannot calculate consumed calories.");
            return 0.0;
        }
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Ensure baseMapper (MealLogMapper) is accessible and has the method
        Double totalCalories = baseMapper.sumCaloriesByUserIdAndDateRange(userId, startOfDay, endOfDay);

        return totalCalories != null ? totalCalories : 0.0;
    }

    @Override
    public Page<MealLog> getMealLogsByUserId(Long userId, int pageNum, int pageSize) {
        Page<MealLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MealLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("meal_time");
        return page(page, queryWrapper);
    }
}