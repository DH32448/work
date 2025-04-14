package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("accountInfo")
public class AccountInfo {
    @TableId(type = IdType.AUTO)
    Integer id;
    @TableField("username")
    String username;
    @TableField("phone")
    String phone;
    @TableField("password")
    String password;
    @TableField("email")
    String email;
    @TableField("role")
    String role;
}
