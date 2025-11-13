package com.ticket;

import com.ticket.mapper.UserMapper;
import com.ticket.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TicketApplicationTest {
    @Resource
    private UserMapper userMapper;
    @Test
    public void test() {
        System.out.println(userMapper);
        User user = new User(null, "admin", "admin", "admin@qq.com", "123456", "1.jpg", 1, null, null, null);
        userMapper.insert(user);
    }
}
