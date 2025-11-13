package com.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dkb
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
