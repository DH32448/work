package com.example.config;

import com.example.entity.vo.RestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e, HttpServletRequest request) {
        logger.error("文件上传请求失败: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        return RestBean.failure(400, "文件上传处理失败: " + getRootCauseMessage(e)).asJsonString();
    }
    
    /**
     * 处理大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.error("上传文件超过大小限制", e);
        return RestBean.failure(413, "上传文件超过大小限制，最大允许100MB").asJsonString();
    }
    
    /**
     * 处理EOFException异常，通常是客户端中断连接
     */
    @ExceptionHandler(EOFException.class)
    public String handleEOFException(EOFException e) {
        logger.error("文件上传过程中连接意外中断", e);
        return RestBean.failure(499, "文件上传过程中连接被中断，请重试").asJsonString();
    }
    
    /**
     * 处理SocketException异常，通常是网络连接问题
     */
    @ExceptionHandler(SocketException.class)
    public String handleSocketException(SocketException e) {
        logger.error("网络连接异常", e);
        return RestBean.failure(500, "网络连接异常，请检查网络并重试").asJsonString();
    }
    
    /**
     * 处理所有IO异常
     */
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException e) {
        logger.error("IO操作异常", e);
        return RestBean.failure(500, "服务器IO操作异常: " + e.getMessage()).asJsonString();
    }
    
    /**
     * 获取根本异常信息
     */
    private String getRootCauseMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }
} 