package org.example.knowmateadmin.service;

import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.entity.UserInfo;
// import com.baomidou.mybatisplus.extension.service.IService; // Ensure IService is removed

// Interface no longer extends IService<UserInfo>
public interface UserInfoService {

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo getUserInfo(String username);

    /**
     * 保存或更新用户信息
     * 
     * @param username 用户名
     * @param dto      用户信息 DTO
     */
    void saveOrUpdate(String username, UserInfoDTO dto);

    /**
     * 根据用户名获取用户信息 DTO
     * 
     * @param username 用户名
     * @return 用户信息 DTO
     */
    UserInfoDTO getUserInfoDTO(String username);

    /**
     * 根据用户 ID 获取用户信息
     * 
     * @param userId 用户 ID
     * @return 用户信息
     */
    UserInfo getUserInfoByUserId(Long userId);

    /**
     * Creates or updates user information and returns the DTO representation.
     * This method should handle merging User and UserInfo entities.
     * 
     * @param username    The username.
     * @param userInfoDTO UserInfoDTO containing data to save/update, including age
     *                    and gender.
     * @return Updated UserInfoDTO reflecting changes in both User and UserInfo
     *         entities.
     */
    UserInfoDTO saveOrUpdateUserInfoDTO(String username, UserInfoDTO userInfoDTO);

    /**
     * Retrieves UserInfo as a DTO by username.
     * 
     * @param username The username.
     * @return UserInfoDTO or null if not found.
     * @deprecated Prefer getUserInfoDTO for clarity, though functionality might be
     *             similar.
     */
    @Deprecated
    UserInfoDTO getUserInfoDTOByUsername(String username);

    /**
     * Retrieves UserInfo entity by username, specifically for
     * DietRecommendationService.
     * The returned UserInfo is from com.example.smartdietplanner.entity.
     * 
     * @param username The username.
     * @return UserInfo object (from com.example.smartdietplanner.entity) or null.
     */
    UserInfo getUserInfoByUsername(String username);
}