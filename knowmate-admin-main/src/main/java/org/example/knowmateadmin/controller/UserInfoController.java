package org.example.knowmateadmin.controller;

import lombok.RequiredArgsConstructor;
import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.service.UserInfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    /**
     * 获取用户健康档案
     */
    @GetMapping("/info")
    @PreAuthorize("hasRole(\'USER\')")
    public ApiResponse<UserInfoDTO> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        UserInfoDTO userInfoDTO = userInfoService.getUserInfoDTO(username);
        return ApiResponse.success(userInfoDTO);
    }

    /**
     * 更新用户健康档案
     */
    @PutMapping("/info")
    @PreAuthorize("hasRole(\'USER\')")
    public ApiResponse<UserInfoDTO> updateUserInfo(
            Authentication authentication,
            @RequestBody UserInfoDTO userInfoDTO) {
        String username = authentication.getName();
        UserInfoDTO updatedDTO = userInfoService.saveOrUpdateUserInfoDTO(username, userInfoDTO);
        return ApiResponse.success(updatedDTO);
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/info/{username}")
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ApiResponse<UserInfo> getUserInfoByUsername(@PathVariable String username) {
        UserInfo userInfo = userInfoService.getUserInfo(username);
        return ApiResponse.success(userInfo);
    }
}