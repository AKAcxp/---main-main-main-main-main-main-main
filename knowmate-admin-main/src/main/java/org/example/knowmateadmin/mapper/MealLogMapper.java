package org.example.knowmateadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.knowmateadmin.entity.MealLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MealLogMapper extends BaseMapper<MealLog> {
    // You can add custom query methods here if needed

    /**
     * 计算指定日期范围内用户的总卡路里摄入
     * （使用 COALESCE 函数确保在无记录时返回 0.0）
     */
    @Select("SELECT COALESCE(SUM(calories), 0.0) FROM meal_log WHERE user_id = #{userId} AND meal_time BETWEEN #{startTime} AND #{endTime}")
    Double sumCaloriesByUserIdAndDateRange(@Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 汇总指定日期范围内用户的宏量营养素摄入
     */
    @Select("""
            SELECT
                COALESCE(SUM(f.protein), 0.0) AS total_protein,
                COALESCE(SUM(f.carbohydrate), 0.0) AS total_carbohydrates,
                COALESCE(SUM(f.fat), 0.0) AS total_fat
            FROM meal_log ml
            JOIN food f ON ml.food_name = f.name
            WHERE ml.user_id = #{userId}
            AND ml.meal_time BETWEEN #{startTime} AND #{endTime}
            """)
    // 返回一个 Map，因为我们返回的是汇总值而不是实体列表
    Map<String, Double> sumNutritionByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}