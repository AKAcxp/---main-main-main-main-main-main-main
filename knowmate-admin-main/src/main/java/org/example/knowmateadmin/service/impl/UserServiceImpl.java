package org.example.knowmateadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.knowmateadmin.dto.AdminUserDTO;
import org.example.knowmateadmin.dto.LoginDTO;
import org.example.knowmateadmin.dto.LoginResponseDTO;
import org.example.knowmateadmin.dto.RegisterDTO;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.UserMapper;
import org.example.knowmateadmin.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.knowmateadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @projectName: knowmate-admin
 * @package: org.example.knowmateadmin.service.impl
 * @className: UserServiceImpl
 * @author: LISIR
 * @description: TODO
 * @date: 2025/5/1 10:44
 * @version: 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        if (user.getIsEnabled() != null && !user.getIsEnabled()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", registerDTO.getUsername()).or().eq("email", registerDTO.getEmail());
        if (count(queryWrapper) > 0) {
            throw new RuntimeException("用户名或邮箱已存在");
        }

        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setCreateTime(java.time.LocalDateTime.now());
        newUser.setUpdateTime(java.time.LocalDateTime.now());
        newUser.setRole("ROLE_USER");
        newUser.setIsEnabled(true);

        if (registerDTO.getAge() != null) {
            newUser.setAge(registerDTO.getAge());
        }
        if (registerDTO.getGender() != null) {
            newUser.setGender(registerDTO.getGender());
        }

        if (registerDTO.getHeight() != null && registerDTO.getWeight() != null) {
            newUser.setHeight(registerDTO.getHeight());
            newUser.setWeight(registerDTO.getWeight());
            calculateBmiAndStatus(newUser);
        }

        save(newUser);
    }

    /**
     * 计算 BMI 和健康状态
     *
     * @param user 用户对象
     */
    private void calculateBmiAndStatus(User user) {
        if (user.getHeight() == null || user.getWeight() == null || user.getHeight() <= 0 || user.getWeight() <= 0) {
            user.setBmi(null);
            user.setBmiStatus("未计算");
            return;
        }

        double heightInMeter = user.getHeight() / 100.0; // 将身高从厘米转换为米
        double bmi = user.getWeight() / (heightInMeter * heightInMeter);
        user.setBmi(Math.round(bmi * 100.0) / 100.0); // 保留两位小数

        String bmiStatus;
        if (bmi < 18.5) {
            bmiStatus = "偏瘦";
        } else if (bmi >= 18.5 && bmi < 24) {
            bmiStatus = "正常";
        } else if (bmi >= 24 && bmi < 28) {
            bmiStatus = "超重";
        } else {
            bmiStatus = "肥胖";
        }
        user.setBmiStatus(bmiStatus);
    }

    @Override
    public LoginResponseDTO login(LoginDTO loginDTO, AuthenticationManager authenticationManager) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object principal = authentication.getPrincipal();
        User userDetails;
        if (principal instanceof User) {
            userDetails = (User) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) principal;
            userDetails = userMapper.selectOne(new QueryWrapper<User>().eq("username", springUser.getUsername()));
        } else {
            throw new RuntimeException("Unexpected principal type: " + principal.getClass().getName());
        }

        if (userDetails == null) {
            throw new RuntimeException("User details not found after authentication.");
        }

        String token = jwtUtil.generateToken(userDetails.getId(), userDetails.getUsername(), userDetails.getRole());

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(token);
        responseDTO.setRole(userDetails.getRole());
        responseDTO.setMessage("登录成功");
        return responseDTO;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public Page<AdminUserDTO> getAllUsers(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        page = userMapper.selectPage(page, null);

        List<AdminUserDTO> adminUserDTOs = page.getRecords().stream().map(user -> {
            AdminUserDTO dto = new AdminUserDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            dto.setIsEnabled(user.getIsEnabled());
            dto.setCreateTime(user.getCreateTime());
            dto.setUpdateTime(user.getUpdateTime());
            return dto;
        }).collect(Collectors.toList());

        Page<AdminUserDTO> adminUserDTOPage = new Page<>(pageNum, pageSize);
        adminUserDTOPage.setRecords(adminUserDTOs);
        adminUserDTOPage.setTotal(page.getTotal());
        adminUserDTOPage.setCurrent(page.getCurrent());
        adminUserDTOPage.setSize(page.getSize());
        return adminUserDTOPage;
    }

    @Override
    public void updateUserRole(Long userId, String newRole) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRole(newRole);
        userMapper.updateById(user);
    }

    @Override
    public void enableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setIsEnabled(true);
        userMapper.updateById(user);
    }

    @Override
    public void disableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setIsEnabled(false);
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }
}
