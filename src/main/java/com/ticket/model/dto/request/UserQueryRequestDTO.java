package com.ticket.model.dto.request;

import lombok.Data;

@Data
public class UserQueryRequestDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    // 用户名模糊查询
    private String username;
    // 邮箱精确查询
    private String email;
    // 状态：1-正常，0-禁用
    private Integer status;
}
