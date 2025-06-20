package org.example.knowmateadmin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.knowmateadmin.entity.Food;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
    /**
     * 查询高蛋白食物
     */
    List<Food> selectHighProteinFoods(@Param("preference") String preference, @Param("limit") int limit);

    /**
     * 查询低卡路里低脂肪食物
     */
    List<Food> selectLowCalorieLowFatFoods(@Param("preference") String preference, @Param("limit") int limit);

    /**
     * 查询营养均衡的食 
     */
    List<Food> selectBalancedFoods(@Param("preference") String preference, @Param("limit") int limit);
}