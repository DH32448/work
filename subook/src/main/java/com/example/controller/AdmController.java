package com.example.controller;

import com.example.entity.vo.RestBean;
import com.example.service.CarouselService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RequestMapping("/api/adm")
@RestController
public class AdmController {
    private static final Logger logger = LoggerFactory.getLogger(AdmController.class);
    
    @Resource
    CarouselService carouselService;
    
    @PostMapping("/addimg")
    public String addImg(@RequestParam(value = "file", required = true) MultipartFile file,
                         @RequestParam String title,
                         @RequestParam String text) {
        logger.info("开始处理文件上传请求，文件名：{}，大小：{} 字节", 
                file.getOriginalFilename(),
                file.getSize());
        try {
            return carouselService.addImg(file, title, text);
        } catch (Exception e) {
            logger.error("文件上传处理失败", e);
            return RestBean.failure(500, "文件上传失败: " + e.getMessage()).asJsonString();
        }
    }
    
    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e) {
        logger.error("文件上传解析异常", e);
        return RestBean.failure(400, "文件上传请求解析失败: " + e.getMessage()).asJsonString();
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.error("上传文件超过大小限制", e);
        return RestBean.failure(413, "上传文件超过大小限制，最大允许100MB").asJsonString();
    }
}
