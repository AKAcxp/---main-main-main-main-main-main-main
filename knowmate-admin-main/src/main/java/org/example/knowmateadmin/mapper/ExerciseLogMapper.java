package org.example.knowmateadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.knowmateadmin.entity.ExerciseLog;

import java.time.LocalDateTime;

@Mapper
public interface ExerciseLogMapper extends BaseMapper<ExerciseLog> {
        // You can add custom query methods here if needed

        /**
         * 计算指定日期范围内用户的总卡路里消耗
         *
         * @param userId    用户ID
         * @param startTime 开始时间
         * @param endTime   结束时间
         * @return 总卡路里消耗量
         */
        @Select("SELECT COALESCE(SUM(calories_burned), 0.0) FROM exercise_log WHERE user_id = #{userId} AND exercise_time BETWEEN #{startTime} AND #{endTime}")
        Double sumCaloriesBurnedForDateRange(@Param("userId") Long userId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
}