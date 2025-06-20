package org.example.knowmateadmin.mapper;

import org.example.knowmateadmin.entity.Food;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
    List<Food> selectHighProteinFoods(@Param("preference") String preference, @Param("limit") int limit);

    List<Food> selectLowCalorieLowFatFoods(@Param("preference") String preference, @Param("limit") int limit);

    List<Food> selectBalancedFoods(@Param("preference") String preference, @Param("limit") int limit);
}