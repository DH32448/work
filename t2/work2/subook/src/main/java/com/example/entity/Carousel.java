package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Carousel") // 与数据库表名映射
public class Carousel{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO) // 自增主键
    private Integer id;

    @TableField("uuid")
    private String uuid;

    @TableField("title")
    private String title;

    @TableField("text")
    private String text;

    @TableField("imgdata")
    private byte[] imgData;

}
