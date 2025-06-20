package org.example.knowmateadmin.service;

import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.entity.UserInfo;

public interface UserInfoService {
    UserInfo getUserInfo(String username);

    void saveOrUpdate(String username, UserInfoDTO dto);
}