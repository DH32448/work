package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Carousel;
import com.example.entity.vo.RestBean;
import com.example.mapper.CarouselMapper;
import com.example.service.CarouselService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService  {
    private static final Logger logger = LoggerFactory.getLogger(CarouselServiceImpl.class);
    
    @Resource
    CarouselMapper carouselMapper;
    
    public String addImg(MultipartFile file, String title, String text){
        if (file == null) {
            logger.error("文件对象为空");
            return RestBean.failure(400, "文件对象为空").asJsonString();
        }
        
        if (file.isEmpty()) {
            logger.error("上传的文件为空");
            return RestBean.failure(404, "找不到资源").asJsonString();
        }
        
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            logger.error("不支持的文件类型: {}", contentType);
            return RestBean.failure(400, "仅支持 JPG/PNG 格式").asJsonString();
        }
        
        try {
            // 记录文件大小信息
            logger.info("文件大小: {} 字节", file.getSize());
            
            // 转换为字节数组
            byte[] imageData = file.getBytes();

            // 构建实体对象
            Carousel carousel = new Carousel();
            carousel.setUuid(UUID.randomUUID().toString());
            carousel.setTitle(title);
            carousel.setText(text);
            carousel.setImgData(imageData);
            
            logger.info("准备将图片数据保存到数据库, 标题: {}", title);
            int insert = carouselMapper.insert(carousel);
            
            if (insert > 0) {
                logger.info("图片数据成功保存到数据库, ID: {}", carousel.getId());
                return RestBean.success("数据库插入成功").asJsonString();
            } else {
                logger.error("数据库插入失败");
                return RestBean.failure(500, "数据库插入失败").asJsonString();
            }

        } catch (IOException e) {
            logger.error("处理文件时发生IO异常", e);
            return RestBean.failure(500, "文件处理异常: " + e.getMessage()).asJsonString();
        } catch (Exception e) {
            logger.error("处理文件时发生未知异常", e);
            return RestBean.failure(500, "服务器异常: " + e.getMessage()).asJsonString();
        }
    }
}
