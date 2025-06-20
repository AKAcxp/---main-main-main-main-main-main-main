package org.example.knowmateadmin.controller;

import org.example.knowmateadmin.common.ApiResponse;
import org.example.knowmateadmin.dto.AdminUserDTO;
import org.example.knowmateadmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Page<AdminUserDTO>> getAllUsers(@RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<AdminUserDTO> usersPage = userService.getAllUsers(pageNum, pageSize);
        return ApiResponse.success(usersPage);
    }

    @PutMapping("/role/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String newRole = payload.get("newRole");
        if (newRole == null || newRole.isEmpty()) {
            return ApiResponse.error(400, "新角色不能为空");
        }
        userService.updateUserRole(userId, newRole);
        return ApiResponse.success("用户角色更新成功");
    }

    @PutMapping("/enable/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> enableUser(@PathVariable Long userId) {
        log.info("AdminUserController: Received request to enable user with ID: {}", userId);
        userService.enableUser(userId);
        return ApiResponse.success("用户账户已启用");
    }

    @PutMapping("/disable/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> disableUser(@PathVariable Long userId) {
        log.info("AdminUserController: Received request to disable user with ID: {}", userId);
        userService.disableUser(userId);
        return ApiResponse.success("用户账户已禁用");
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success("用户已删除");
    }
}