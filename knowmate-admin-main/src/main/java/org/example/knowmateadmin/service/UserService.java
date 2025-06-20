package org.example.knowmateadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.knowmateadmin.dto.AdminUserDTO;
import org.example.knowmateadmin.dto.LoginDTO;
import org.example.knowmateadmin.dto.LoginResponseDTO;
import org.example.knowmateadmin.dto.RegisterDTO;
import org.example.knowmateadmin.entity.User;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * @projectName: knowmate-admin
 * @package: org.example.knowmateadmin.service
 * @className: UserService
 * @author: LISIR
 * @description: TODO
 * @date: 2025/5/1 10:37
 * @version: 1.0
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * 
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     * 
     * @param loginDTO              登录信息
     * @param authenticationManager 认证管理器
     * @return 登录成功返回 LoginResponseDTO，包含 JWT Token 和用户角色
     */
    LoginResponseDTO login(LoginDTO loginDTO, AuthenticationManager authenticationManager);

    User findByUsername(String username);

    /**
     * 分页获取所有用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户列表的分页信息
     */
    Page<AdminUserDTO> getAllUsers(int pageNum, int pageSize);

    /**
     * 更新用户角色
     *
     * @param userId  用户ID
     * @param newRole 新角色
     */
    void updateUserRole(Long userId, String newRole);

    /**
     * 启用用户账户
     *
     * @param userId 用户ID
     */
    void enableUser(Long userId);

    /**
     * 禁用用户账户
     *
     * @param userId 用户ID
     */
    void disableUser(Long userId);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
}
