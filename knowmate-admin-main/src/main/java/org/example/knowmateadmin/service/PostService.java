package org.example.knowmateadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.knowmateadmin.dto.PostDTO;
import org.example.knowmateadmin.entity.Post;
import org.example.knowmateadmin.dto.PostRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户动态服务接口
 */
public interface PostService {

    /**
     * 创建新动态
     * 
     * @param userId  用户ID
     * @param postDTO 动态信息
     * @return 创建结果
     */
    void createPost(Long userId, PostRequestDTO postDTO);

    /**
     * 分页查询动态列表
     * 
     * @param page 分页参数
     * @return 动态列表
     */
    IPage<PostDTO> getPostPage(Page<Post> page);

    /**
     * 根据ID查询动态详情
     * 
     * @param postId 动态ID
     * @return 动态详情
     */
    PostDTO getPostById(Long postId);

    /**
     * 根据用户ID查询该用户的所有动态
     * 
     * @param userId 用户ID
     * @return 该用户的动态列表
     */
    List<PostDTO> getPostsByUserId(Long userId);

    /**
     * 更新动态
     * 
     * @param postDTO 动态信息
     * @return 更新结果
     */
    boolean updatePost(PostDTO postDTO);

    /**
     * 删除动态
     * 
     * @param postId 动态ID
     * @return 删除结果
     */
    boolean deletePost(Long postId);

    /**
     * 获取用户的动态列表
     * 
     * @param userId 用户ID
     * @return 动态列表
     */
    List<Post> getPostsByUser(Long userId);

    /**
     * 上传媒体文件
     * 
     * @param file 文件
     * @return 文件URL
     */
    String upload(MultipartFile file);
}