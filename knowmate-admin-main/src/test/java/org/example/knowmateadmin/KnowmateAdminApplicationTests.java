package org.example.knowmateadmin;

import jakarta.annotation.Resource;
import org.example.knowmateadmin.entity.User;
import org.example.knowmateadmin.mapper.UserMapper;
import org.example.knowmateadmin.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class KnowmateAdminApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    public void testUserMapper() {
        System.out.println(("----- selectAll method test ------"));
        User user = userMapper.selectById(1);
        System.out.println(user);
    }

    @Test
    public void testUserService() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userService.list();
        userList.forEach(System.out::println);
    }

}
