package org.example.knowmateadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.knowmateadmin.entity.ChatLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatLogMapper extends BaseMapper<ChatLog> {
}