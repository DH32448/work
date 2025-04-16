package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Carousel;
import org.springframework.web.multipart.MultipartFile;

public interface CarouselService extends IService<Carousel> {
    String addImg(MultipartFile file, String title, String text);
}
