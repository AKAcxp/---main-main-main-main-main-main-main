package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.knowmateadmin.dto.PostDTO;
import org.example.knowmateadmin.dto.PostRequestDTO;
import org.example.knowmateadmin.entity.Post;
import org.example.knowmateadmin.mapper.PostMapper;
import org.example.knowmateadmin.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户动态服务实现类
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final PostMapper postMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    public PostServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public void createPost(Long userId, PostRequestDTO postDTO) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());

        // 将媒体URL列表转换为JSON字符串
        if (postDTO.getMediaUrls() != null && !postDTO.getMediaUrls().isEmpty()) {
            try {
                post.setMediaUrls(objectMapper.writeValueAsString(postDTO.getMediaUrls()));
            } catch (JsonProcessingException e) {
                // 如果转换失败，设置为空JSON数组
                post.setMediaUrls("[]");
            }
        } else {
            post.setMediaUrls("[]");
        }

        save(post);
    }

    @Override
    public IPage<PostDTO> getPostPage(Page<Post> page) {
        return postMapper.selectPostsWithUserInfo(page);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        return postMapper.selectPostDetailById(postId);
    }

    @Override
    public List<PostDTO> getPostsByUserId(Long userId) {
        return postMapper.selectPostsByUserId(userId);
    }

    @Override
    @Transactional
    public boolean updatePost(PostDTO postDTO) {
        Post post = getById(postDTO.getId());
        if (post == null) {
            return false;
        }

        // 只允许更新标题、内容和媒体URL
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // 将媒体URL列表转换为JSON字符串
        if (postDTO.getMediaUrls() != null && !postDTO.getMediaUrls().isEmpty()) {
            try {
                post.setMediaUrls(objectMapper.writeValueAsString(postDTO.getMediaUrls()));
            } catch (JsonProcessingException e) {
                // 如果转换失败，保持原值不变
            }
        }

        return updateById(post);
    }

    @Override
    @Transactional
    public boolean deletePost(Long postId) {
        return removeById(postId);
    }

    @Override
    public List<Post> getPostsByUser(Long userId) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 返回文件URL
            return "/uploads/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}