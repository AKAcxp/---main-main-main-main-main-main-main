package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.knowmateadmin.dto.ExerciseLogDTO;
import org.example.knowmateadmin.entity.ExerciseLog;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.ExerciseLogMapper;
import org.example.knowmateadmin.service.ExerciseLogService;
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
public class ExerciseLogServiceImpl extends ServiceImpl<ExerciseLogMapper, ExerciseLog> implements ExerciseLogService {

    private final UserService userService;

    @Autowired
    public ExerciseLogServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public ExerciseLog addExerciseLog(String username, ExerciseLogDTO exerciseLogDTO) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        ExerciseLog exerciseLog = new ExerciseLog();
        exerciseLog.setUserId(user.getId());
        exerciseLog.setActivity(exerciseLogDTO.getActivity());
        exerciseLog.setDuration(exerciseLogDTO.getDuration());
        exerciseLog.setCaloriesBurned(exerciseLogDTO.getCaloriesBurned());
        exerciseLog.setExerciseTime(
                exerciseLogDTO.getExerciseTime() != null ? exerciseLogDTO.getExerciseTime() : LocalDateTime.now());
        exerciseLog.setCreatedAt(LocalDateTime.now());
        // exerciseLog.setUpdatedAt(LocalDateTime.now()); // If entity has this field
        save(exerciseLog);
        return exerciseLog;
    }

    @Override
    public List<ExerciseLog> getExerciseLogsByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return Collections.emptyList(); // Or throw exception
        }
        return getExerciseLogsByUserId(user.getId());
    }

    @Override
    public List<ExerciseLog> getExerciseLogsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(new QueryWrapper<ExerciseLog>().eq("user_id", userId).orderByDesc("exercise_time"));
    }

    @Override
    public List<ExerciseLog> getExerciseLogsByUserIdAndDate(Long userId, LocalDate date) {
        if (userId == null || date == null) {
            return Collections.emptyList();
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return list(new QueryWrapper<ExerciseLog>()
                .eq("user_id", userId)
                .between("exercise_time", startOfDay, endOfDay)
                .orderByAsc("exercise_time"));
    }

    @Override
    @Transactional
    public void deleteExerciseLog(Long exerciseLogId, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        remove(new QueryWrapper<ExerciseLog>().eq("id", exerciseLogId).eq("user_id", user.getId()));
    }

    @Override
    public double getTodayTotalCaloriesBurnedByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            log.warn("User not found for calorie calculation: " + username);
            return 0.0;
        }
        return getTodayTotalCaloriesBurnedByUserId(user.getId());
    }

    // Implement other specific service methods if needed
    @Override
    public double getTodayTotalCaloriesBurnedByUserId(Long userId) {
        if (userId == null) {
            log.warn("User ID is null, cannot calculate burned calories.");
            return 0.0;
        }
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // 调用 Mapper 的新方法
        Double totalCaloriesBurned = baseMapper.sumCaloriesBurnedForDateRange(userId, startOfDay,
                endOfDay);

        return totalCaloriesBurned != null ? totalCaloriesBurned : 0.0;
    }
}