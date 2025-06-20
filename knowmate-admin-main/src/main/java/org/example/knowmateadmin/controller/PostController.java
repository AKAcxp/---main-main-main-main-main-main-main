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
    public ApiResponse<List<Post>> getMyPosts(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromRequest(request);
        return ApiResponse.success(postService.getPostsByUser(userId));
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