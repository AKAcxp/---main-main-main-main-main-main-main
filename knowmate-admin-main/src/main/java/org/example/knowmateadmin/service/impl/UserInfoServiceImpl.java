package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.knowmateadmin.entity.UserInfo;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.UserInfoMapper;
import org.example.knowmateadmin.mapper.UserMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.example.knowmateadmin.dto.UserInfoDTO;
import org.example.knowmateadmin.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserInfo getUserInfo(String username) {
        logger.info("SERVICE GET: Attempting to load user info for: {}", username);
        return userInfoMapper.selectByUsername(username);
    }

    @Override
    public void saveOrUpdate(String username, UserInfoDTO dto) {
        UserInfo existing = userInfoMapper.selectByUsername(username);
        UserInfo info = new UserInfo();
        info.setUsername(username);
        info.setHeight(dto.getHeight());
        info.setWeight(dto.getWeight());
        info.setGoal(dto.getGoal());
        info.setPreference(dto.getPreference());
        info.setAvoidFood(dto.getAvoidFood());

        if (existing == null) {
            userInfoMapper.insert(info);
        } else {
            userInfoMapper.update(info);
        }
    }

    @Override
    @Transactional
    public UserInfoDTO saveOrUpdateUserInfoDTO(String username, UserInfoDTO userInfoDTO) {
        logger.info("SERVICE saveOrUpdateUserInfoDTO: Attempting to save/update for username: {}", username);
        logger.debug("SERVICE saveOrUpdateUserInfoDTO: Received DTO: {}", userInfoDTO.toString());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            logger.error("SERVICE saveOrUpdateUserInfoDTO: User not found with username: {}. Cannot update.", username);
            return null;
        }

        boolean userChanged = false;
        if (userInfoDTO.getAge() != null && !userInfoDTO.getAge().equals(user.getAge())) {
            user.setAge(userInfoDTO.getAge());
            userChanged = true;
        }
        if (userInfoDTO.getGender() != null && !userInfoDTO.getGender().equals(user.getGender())) {
            user.setGender(userInfoDTO.getGender());
            userChanged = true;
        }

        if (userChanged) {
            logger.info("SERVICE saveOrUpdateUserInfoDTO: Updating User entity for {}", username);
            userMapper.updateById(user);
        }

        UserInfo userInfoEntity = userInfoMapper.selectByUsername(username);
        boolean isNewUserInfo = (userInfoEntity == null);

        if (isNewUserInfo) {
            logger.info("SERVICE saveOrUpdateUserInfoDTO: Creating new UserInfo entity for {}", username);
            userInfoEntity = new UserInfo();
            userInfoEntity.setUsername(username);
            userInfoEntity.setUserId(user.getId());
        }

        userInfoEntity.setHeight(userInfoDTO.getHeight());
        userInfoEntity.setWeight(userInfoDTO.getWeight());
        userInfoEntity.setGoal(userInfoDTO.getGoal());
        userInfoEntity.setPreference(userInfoDTO.getPreference());
        userInfoEntity.setAvoidFood(userInfoDTO.getAvoidFood());

        if (userInfoEntity.getHeight() != null && userInfoEntity.getWeight() != null &&
                userInfoEntity.getHeight() > 0 && userInfoEntity.getWeight() > 0) {
            double heightInMeter = userInfoEntity.getHeight() / 100.0;
            double bmi = userInfoEntity.getWeight() / (heightInMeter * heightInMeter);
            userInfoEntity.setBmi(Math.round(bmi * 100.0) / 100.0);
            userInfoEntity.setBmiStatus(determineBmiStatus(userInfoEntity.getBmi()));
        } else {
            userInfoEntity.setBmi(null);
            userInfoEntity.setBmiStatus("未计算");
        }
        userInfoEntity.setUpdatedAt(LocalDateTime.now());

        if (isNewUserInfo) {
            userInfoMapper.insert(userInfoEntity);
            logger.info("SERVICE saveOrUpdateUserInfoDTO: Inserted new UserInfo for {}", username);
        } else {
            userInfoMapper.update(userInfoEntity);
            logger.info("SERVICE saveOrUpdateUserInfoDTO: Updated existing UserInfo for {}", username);
        }

        return getUserInfoDTO(username);
    }

    @Override
    public UserInfoDTO getUserInfoDTO(String username) {
        logger.info("SERVICE getUserInfoDTO: Attempting to get DTO for username: {}", username);
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            logger.warn("SERVICE getUserInfoDTO: User not found with username: {}", username);
            return null;
        }

        UserInfo userInfoEntity = userInfoMapper.selectByUsername(username);

        UserInfoDTO dto = new UserInfoDTO();
        dto.setUsername(user.getUsername());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());

        if (userInfoEntity != null) {
            dto.setHeight(userInfoEntity.getHeight());
            dto.setWeight(userInfoEntity.getWeight());
            dto.setGoal(userInfoEntity.getGoal());
            dto.setPreference(userInfoEntity.getPreference());
            dto.setAvoidFood(userInfoEntity.getAvoidFood());
            Double bmi = userInfoEntity.getBmi();
            dto.setBmi(bmi);
            if (bmi != null) {
                dto.setBmiStatus(determineBmiStatus(bmi));
            }
            logger.info("SERVICE getUserInfoDTO: Found UserInfo entity for {}: {}", username,
                    userInfoEntity.toString());
        } else {
            logger.info(
                    "SERVICE getUserInfoDTO: No UserInfo entity found for username: {}. DTO will have nulls for UserInfo fields.",
                    username);
        }
        logger.info("SERVICE getUserInfoDTO: Constructed DTO for {}: {}", username, dto.toString());
        return dto;
    }

    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        logger.info("SERVICE getUserInfoByUserId: Loading entity for user ID: {}", userId);
        try {
            return userInfoMapper.selectByUserId(userId);
        } catch (Exception e) {
            logger.error(
                    "Error calling selectByUserId on UserInfoMapper for userId: {} - Ensure method exists and UserInfo has userId mapping.",
                    userId, e);
            return null;
        }
    }

    @Override
    public UserInfo getUserInfoByUsername(String username) {
        logger.info("SERVICE getUserInfoByUsername: Loading entity for username: {}", username);
        return userInfoMapper.selectByUsername(username);
    }

    @Override
    @Deprecated
    public UserInfoDTO getUserInfoDTOByUsername(String username) {
        return getUserInfoDTO(username);
    }

    private String determineBmiStatus(double bmi) {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi >= 18.5 && bmi < 24) {
            return "正常";
        } else if (bmi >= 24 && bmi < 28) {
            return "超重";
        } else if (bmi >= 28) {
            return "肥胖";
        } else {
            return "未知";
        }
    }
}