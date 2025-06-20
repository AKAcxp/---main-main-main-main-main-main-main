package org.example.knowmateadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.knowmateadmin.dto.LoginDTO;
import org.example.knowmateadmin.dto.LoginResponseDTO;
import org.example.knowmateadmin.dto.RegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.example.knowmateadmin.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private org.example.knowmateadmin.utils.JwtUtil jwtUtil;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserService userService;

    @Operation(summary = "用户注册", description = "通过用户名、邮箱和密码注册新用户", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = RegisterDTO.class))), responses = {
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "参数错误或用户已存在")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) {
        // 1. 基本参数校验 (可以做得更细致)
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名、邮箱和密码不能为空");
        }

        // 2. 检查用户名是否已存在
        if (userMapper.selectOne(new QueryWrapper<User>().eq("username", request.getUsername())) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名已存在");
        }

        // 3. 检查邮箱是否已注册
        if (userMapper.selectOne(new QueryWrapper<User>().eq("email", request.getEmail())) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("邮箱已被注册");
        }

        // 4. 调用 UserService 的注册方法
        try {
            userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("注册成功");
        } catch (RuntimeException e) {
            log.error("用户注册过程中发生错误: 用户名 {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册过程中发生未知错误: 用户名 {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("注册过程中发生错误，请稍后重试");
        }
    }

    @GetMapping(value = "/register", produces = "text/plain;charset=UTF-8")
    public String testRegister() {
        return "注册接口仅支持POST请求，请使用POST方法并提供用户名、邮箱和密码。";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        // 1. 基本参数校验
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名和密码不能为空");
        }

        try {
            // 2. 调用 UserService 的登录方法，并传入 AuthenticationManager
            LoginResponseDTO responseDTO = userService.login(request, authenticationManager);
            return ResponseEntity.ok(Map.of("token", responseDTO.getToken(), "role", responseDTO.getRole(), "message",
                    responseDTO.getMessage()));
        } catch (RuntimeException e) {
            log.warn("登录失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("登录过程中发生未知错误: 用户名 {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录过程中发生错误，请稍后重试");
        }
    }
}