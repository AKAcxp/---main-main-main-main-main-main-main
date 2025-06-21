package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.PostRequestDTO;
import org.example.knowmateadmin.entity.Post;
import org.example.knowmateadmin.service.PostService;
import org.example.knowmateadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.knowmateadmin.dto.PostDTO;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @PostMapping
    public ApiResponse<?> createPost(@RequestBody PostRequestDTO postDTO, HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        postService.createPost(userId, postDTO);
        return ApiResponse.success("发布成功");
    }

    @GetMapping("/my")
    public ApiResponse getMyPosts(HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getUserIdFromRequest(request);
            logger.info("获取用户ID为 {} 的动态列表", userId);

            List<PostDTO> posts = postService.getPostsByUserId(userId);
            logger.info("获取到原始帖子数量: {}", posts.size());

            // 处理每个帖子的mediaUrls
            for (PostDTO post : posts) {
                List<String> mediaUrls = post.getMediaUrls();
                logger.info("帖子ID: {}, 原始mediaUrls: {}", post.getId(), mediaUrls);

                // 确保mediaUrls不为null
                if (mediaUrls == null) {
                    mediaUrls = new ArrayList<>();
                    post.setMediaUrls(mediaUrls);
                    logger.info("帖子ID: {}, 设置空mediaUrls列表", post.getId());
                    continue; // 如果为null，设置为空列表后直接处理下一个帖子
                }

                // 过滤掉null或空字符串的URL
                List<String> filteredUrls = new ArrayList<>();
                for (String url : mediaUrls) {
                    if (url != null && !url.trim().isEmpty() && !"null".equals(url)) {
                        filteredUrls.add(url);
                    }
                }

                // 处理URL路径，确保正确的格式
                List<String> processedUrls = new ArrayList<>();
                for (String url : filteredUrls) {
                    // 确保URL格式正确
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        // 确保URL以/uploads/开头
                        if (!url.startsWith("/uploads/")) {
                            url = "/uploads/" + url;
                        }
                        logger.info("帖子ID: {}, 处理后的URL: {}", post.getId(), url);
                    }
                    processedUrls.add(url);
                }

                post.setMediaUrls(processedUrls);
                logger.info("帖子ID: {}, 处理后mediaUrls: {}", post.getId(), processedUrls);
            }

            logger.info("成功获取到 {} 条动态", posts.size());
            return ApiResponse.success(posts);
        } catch (Exception e) {
            logger.error("获取动态列表失败", e);
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            logger.info("接收到文件上传请求: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
            if (file.isEmpty()) {
                logger.error("上传失败：文件为空");
                return ApiResponse.error(400, "上传失败：文件为空");
            }

            String url = postService.upload(file);
            logger.info("文件上传成功: {}", url);
            return ApiResponse.success(url);
        } catch (Exception e) {
            logger.error("文件上传异常: ", e);
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
}