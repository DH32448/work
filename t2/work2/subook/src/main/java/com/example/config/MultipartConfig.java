//package com.example.config;
//
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.util.unit.DataSize;
//import org.springframework.web.multipart.MultipartResolver;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.multipart.support.StandardServletMultipartResolver;
//
//import javax.servlet.MultipartConfigElement;
//import java.io.File;
//
///**
// * 文件上传配置类
// */
@Configuration
public class MultipartConfig {
//
//    /**
//     * 配置Multipart文件上传元素
//     */
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        // 设置文件最大限制为100MB
//        factory.setMaxFileSize(DataSize.ofMegabytes(100));
//        // 设置请求最大限制为200MB
//        factory.setMaxRequestSize(DataSize.ofMegabytes(200));
//
//        // 获取系统临时目录
//        String tempDir = System.getProperty("java.io.tmpdir");
//        File tmpDir = new File(tempDir);
//
//        // 如果临时目录不存在则创建
//        if (!tmpDir.exists()) {
//            tmpDir.mkdirs();
//        }
//
//        factory.setLocation(tempDir);
//        return factory.createMultipartConfig();
//    }
//
//    /**
//     * 配置标准Servlet的MultipartResolver
//     */
//    @Bean
//    public MultipartResolver multipartResolver() {
//        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
//        // 延迟处理文件解析，对大文件上传很重要
//        resolver.setResolveLazily(true);
//        return resolver;
//    }
}