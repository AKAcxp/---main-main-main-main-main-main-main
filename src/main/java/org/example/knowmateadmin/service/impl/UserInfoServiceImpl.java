package org.example.knowmateadmin.service.impl;

import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.mapper.UserInfoMapper;
import org.example.knowmateadmin.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper mapper;

    @Override
    public UserInfo getUserInfo(String username) {
        return mapper.selectByUsername(username);
    }

    @Override
    public void saveOrUpdate(String username, UserInfoDTO dto) {
        UserInfo existing = mapper.selectByUsername(username);
        UserInfo info = new UserInfo();
        info.setUsername(username);
        info.setHeight(dto.getHeight());
        info.setWeight(dto.getWeight());
        info.setGoal(dto.getGoal());
        info.setPreference(dto.getPreference());
        info.setAvoidFood(dto.getAvoidFood());

        if (existing == null) {
            mapper.insert(info);
        } else {
            mapper.update(info);
        }
    }
}