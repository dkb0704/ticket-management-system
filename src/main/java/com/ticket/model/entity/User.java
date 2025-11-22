package com.ticket.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author dkb
 */
@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public static final Integer ROLE_ADMIN = 0; // 管理员角色
    public static final Integer ROLE_USER = 1;  // 普通用户角色

    @TableId(type = IdType.AUTO)
    // 核心用户ID
    private Long id;
    // 用户名（可为空）
    private String username;
    // 加密密码（可为空，第三方登录无密码）
    private String password;
    // 邮箱（可为空）
    private String email;
    // 手机号（可为空）
    private String phone;
    // 头像
    private String avatar;
    // 状态：1-正常，0-禁用
    private Integer status;
    // 默认普通用户
    private Integer role = ROLE_USER;
    // 最后登录时间
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}