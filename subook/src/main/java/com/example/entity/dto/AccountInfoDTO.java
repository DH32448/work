package com.example.entity.dto;

import com.example.entity.AccountInfo;
import com.example.entity.ot.SexEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private int id;
    private int aid;
    private String name;
    private SexEnum sex;
    private Integer age;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;
    
    private String text;
    
    // 存储Base64编码的图片
    private String imgBase64;
    
    // 从AccountInfo转换为AccountInfoDTO
    public static AccountInfoDTO fromAccountInfo(AccountInfo info) {
        AccountInfoDTO dto = new AccountInfoDTO();
        dto.setId(info.getId());
        dto.setAid(info.getAid());
        dto.setName(info.getName());
        dto.setSex(info.getSex());
        dto.setAge(info.getAge());
        dto.setRegisterTime(info.getRegisterTime());
        dto.setText(info.getText());
        
        // 转换图片为Base64
        if (info.getImgData() != null && info.getImgData().length > 0) {
            String base64 = java.util.Base64.getEncoder().encodeToString(info.getImgData());
            dto.setImgBase64("data:image/jpeg;base64," + base64);
        }
        
        return dto;
    }
} 