package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.ot.SexEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("accountinfo")
public class AccountInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("aid")
    private Long aid;

    @TableField(value = "name")
    private String name;

    @TableField("sex")
    private SexEnum sex;

    @TableField("age")
    private Integer age;

    @TableField("registertime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    @TableField("text")
    private String text;
}
