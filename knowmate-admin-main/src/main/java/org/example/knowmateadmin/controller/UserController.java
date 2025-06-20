package org.example.knowmateadmin.controller;

import jakarta.annotation.Resource;
import org.example.knowmateadmin.dto.LoginDTO;
import org.example.knowmateadmin.dto.RegisterDTO;
import org.example.knowmateadmin.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @projectName: knowmate-admin
 * @package: org.example.knowmateadmin.controller
 * @className: UserController
 * @author: LISIR
 * @description: TODO
 * @date: 2025/5/1 10:58
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    private UserService userService;

    // @PostMapping("/register")
    // public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
    // try {
    // userService.register(registerDTO);
    // return ResponseEntity.ok("用户注册成功");
    // } catch (RuntimeException e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    // @PostMapping("/login")
    // public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
    // try {
    // String token = userService.login(loginDTO);
    // Map<String, String> response = new HashMap<>();
    // response.put("token", token);
    // return ResponseEntity.ok(response);
    // } catch (RuntimeException e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    /**
     * 获取当前登录用户基本信息
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("未认证");
        }
        // 获取用户名
        String username = authentication.getName();
        // TODO: 这里可以根据实际需求查询数据库，返回更详细的用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", username);
        userInfo.put("roles", authentication.getAuthorities());
        return ResponseEntity.ok(userInfo);
    }
}
