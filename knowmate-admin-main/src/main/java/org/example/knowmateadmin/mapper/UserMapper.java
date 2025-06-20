package org.example.knowmateadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.knowmateadmin.entity.User;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // BaseMapper 已经提供了常用的 CRUD 方法
    // 如果需要自定义 SQL 查询，可以在这里添加方法，并在对应的 XML 文件中实现
} 