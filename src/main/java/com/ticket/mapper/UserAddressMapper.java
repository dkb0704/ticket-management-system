package com.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ticket.model.entity.UserAddress;

import java.util.List;

public interface UserAddressMapper extends BaseMapper<UserAddress> {
    // 根据用户ID查询地址列表
    List<UserAddress> selectByUserId(Long userId);
}
