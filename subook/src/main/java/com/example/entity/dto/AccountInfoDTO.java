package com.example.entity.dto;

import com.example.entity.AccountInfo;
import com.example.entity.ot.SexEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    // 存储Base64编码的图片，因为要保留向后兼容性，我们不直接序列化这个字段
    @JsonIgnore
    private ImageData imageData;
    
    /**
     * 为了向后兼容，保留imgBase64属性
     * @return 完整的Data URL或null
     */
    @JsonProperty("imgBase64")
    public String getImgBase64() {
        return imageData != null ? imageData.getDataUrl() : null;
    }
    
    /**
     * 提供图片MIME类型
     * @return 图片MIME类型或null
     */
    @JsonProperty("imageMimeType")
    public String getImageMimeType() {
        return imageData != null ? imageData.getMimeType() : null;
    }
    
    /**
     * 提供纯Base64字符串（不含前缀）
     * @return Base64编码的字符串或null
     */
    @JsonProperty("imageBase64")
    public String getImageBase64() {
        return imageData != null ? imageData.getBase64String() : null;
    }
    
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
        
        // 使用ImageData处理图片数据
        if (info.getImgData() != null && info.getImgData().length > 0) {
            ImageData imageData = ImageData.fromByteArray(info.getImgData());
            dto.setImageData(imageData);
        }
        
        return dto;
    }
} 