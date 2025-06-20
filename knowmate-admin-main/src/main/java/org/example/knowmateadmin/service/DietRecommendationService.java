package org.example.knowmateadmin.service;

import org.example.knowmateadmin.dto.RecommendationDTO;
import org.example.knowmateadmin.entity.UserInfo;

public interface DietRecommendationService {
    /**
     * 根据用户ID获取饮食推荐
     *
     * @param userId 用户ID
     * @return 推荐信息DTO
     */
    RecommendationDTO getRecommendation(Long userId);

    /**
     * 根据用户ID获取饮食推荐
     *
     * @param userId 用户ID
     * @return 推荐信息DTO
     */
    RecommendationDTO getDietRecommendationByUserId(Long userId);

    /**
     * 获取详细的饮食推荐（根据用户ID）
     *
     * @param userId 用户ID
     * @return 详细的推荐信息DTO
     */
    RecommendationDTO getDetailedDietRecommendationByUserId(Long userId);

    /**
     * 生成个性化饮食推荐
     *
     * @param userId 用户ID
     * @return 推荐信息DTO
     */
    RecommendationDTO generatePersonalizedRecommendation(Long userId);
}