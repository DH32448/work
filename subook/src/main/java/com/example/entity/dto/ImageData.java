package com.example.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

/**
 * 封装Base64编码的图片数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    // 原始图片字节数据
    @JsonIgnore  // 在JSON序列化时忽略此字段，避免返回大量二进制数据
    private byte[] rawData;
    
    // Base64编码后的数据
    private String base64String;
    
    // 图片MIME类型
    private String mimeType = "image/jpeg";
    
    // 完整的Data URL (可在<img>标签src属性中直接使用)
    private String dataUrl;
    
    /**
     * 从字节数组创建ImageData对象
     * @param data 图片二进制数据
     * @return ImageData对象
     */
    public static ImageData fromByteArray(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        
        ImageData imageData = new ImageData();
        imageData.setRawData(data);
        
        // 编码为Base64字符串
        String base64 = Base64.getEncoder().encodeToString(data);
        imageData.setBase64String(base64);
        
        // 生成dataUrl
        imageData.setDataUrl("data:" + imageData.getMimeType() + ";base64," + base64);
        
        return imageData;
    }
    
    /**
     * 从Base64字符串创建ImageData对象
     * @param base64String Base64编码的字符串（不含前缀）
     * @return ImageData对象
     */
    public static ImageData fromBase64String(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        
        ImageData imageData = new ImageData();
        imageData.setBase64String(base64String);
        
        // 解码为字节数组
        try {
            byte[] data = Base64.getDecoder().decode(base64String);
            imageData.setRawData(data);
        } catch (IllegalArgumentException e) {
            // 处理非法Base64字符串
            return null;
        }
        
        // 生成dataUrl
        imageData.setDataUrl("data:" + imageData.getMimeType() + ";base64," + base64String);
        
        return imageData;
    }
    
    /**
     * 从Data URL创建ImageData对象
     * @param dataUrl 完整的Data URL (格式为: data:image/jpeg;base64,xxx)
     * @return ImageData对象
     */
    public static ImageData fromDataUrl(String dataUrl) {
        if (dataUrl == null || dataUrl.isEmpty() || !dataUrl.startsWith("data:")) {
            return null;
        }
        
        ImageData imageData = new ImageData();
        imageData.setDataUrl(dataUrl);
        
        // 解析MIME类型和Base64字符串
        try {
            // 格式: data:image/jpeg;base64,xxx
            String[] parts = dataUrl.split(";base64,");
            if (parts.length != 2) {
                return null;
            }
            
            // 提取MIME类型
            String mimeType = parts[0].substring(5); // 去掉前面的"data:"
            imageData.setMimeType(mimeType);
            
            // 提取Base64字符串
            String base64String = parts[1];
            imageData.setBase64String(base64String);
            
            // 解码为字节数组
            byte[] data = Base64.getDecoder().decode(base64String);
            imageData.setRawData(data);
            
            return imageData;
        } catch (Exception e) {
            // 处理解析异常
            return null;
        }
    }
    
    /**
     * 判断图片数据是否存在
     * @return 如果存在有效的图片数据则返回true，否则返回false
     */
    public boolean hasImageData() {
        return rawData != null && rawData.length > 0;
    }
    
    @Override
    public String toString() {
        return dataUrl != null ? dataUrl : "无图片数据";
    }
} 