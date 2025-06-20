package org.example.knowmateadmin.mapper;

import org.example.knowmateadmin.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /**
     * 根据用户名查询用户信息
     * 
     * @param username 用户名
     * @return 用户信息对象，如果找不到则返回 null
     */
    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息对象，如果找不到则返回 null
     */
    @Select("SELECT ui.* FROM user_info ui JOIN user u ON ui.username = u.username WHERE u.id = #{userId}")
    UserInfo selectByUserId(@Param("userId") Long userId);

    /**
     * 更新用户信息
     * 
     * @param userInfo 用户信息对象
     * @return 影响的行数
     */
    int update(UserInfo userInfo);
}