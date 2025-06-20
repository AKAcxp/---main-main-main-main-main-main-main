package org.example.knowmateadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.knowmateadmin.dto.PostDTO;
import org.example.knowmateadmin.entity.Post;

import java.util.List;

/**
 * 用户动态Mapper接口
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 分页查询动态列表，并关联用户信息
     * 
     * @param page 分页参数
     * @return 带有用户信息的动态列表
     */
    IPage<PostDTO> selectPostsWithUserInfo(Page<Post> page);

    /**
     * 根据用户ID查询该用户的所有动态
     * 
     * @param userId 用户ID
     * @return 该用户的动态列表
     */
    List<PostDTO> selectPostsByUserId(@Param("userId") Long userId);

    /**
     * 根据动态ID查询动态详情，包含用户信息
     * 
     * @param postId 动态ID
     * @return 动态详情
     */
    PostDTO selectPostDetailById(@Param("postId") Long postId);
}