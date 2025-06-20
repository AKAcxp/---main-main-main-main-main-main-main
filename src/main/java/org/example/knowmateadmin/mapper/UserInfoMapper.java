package org.example.knowmateadmin.mapper;

import org.example.knowmateadmin.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    UserInfo selectByUsername(String username);

    int insert(UserInfo info);

    int update(UserInfo info);
}