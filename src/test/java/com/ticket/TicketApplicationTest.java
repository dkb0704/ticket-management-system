package com.ticket;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ticket.mapper.UserMapper;
import com.ticket.model.entity.User;
import com.ticket.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
public class TicketApplicationTest {
    @Resource
    private UserMapper userMapper;
    @Test
    public void test() {
        User user = new User(null, "admin", PasswordUtils.encrypt("admin"), "admin@qq.com", "123456", "1.jpg",1, 2,null,null,null);
        userMapper.insert(user);
    }
}
