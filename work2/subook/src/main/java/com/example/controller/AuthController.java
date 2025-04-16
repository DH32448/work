package com.example.controller;

import com.example.entity.AccountInfo;
import com.example.entity.ot.SexEnum;
import com.example.entity.vo.RestBean;
import com.example.service.AccountDetailsService;
import com.example.service.AccountInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountInfoService infoService;
    
    //获取验证码
    @GetMapping("/ask-code")
    public String code(@RequestParam(value = "email",required = true) String email){
        String code = accountDetailsService.getCode(email);
        System.out.println(code);
        return code;
    }
    
    @PostMapping("/register")
    public String register(@RequestParam(value = "username",required = true) String username,
                           @RequestParam(value = "email",required = true)String email,
                           @RequestParam(value = "code",required = true)String code,
                           @RequestParam(value = "password",required = true)String password
    ){
        String s = accountDetailsService.registerAccountUser(username, email, code, password);
        System.out.println(s);
        return s;
    }
    
    @GetMapping("/info")
    public String info(@RequestParam(value = "info",required = true)String email){
        return infoService.info(email);
    }
    
    @PutMapping("/update-info")
    public String updateInfo(@RequestBody AccountInfo accountInfo) {
        logger.info("接收到用户信息更新请求: {}", accountInfo.getAid());
        return infoService.infoUpdate(accountInfo);
    }
    
    @PostMapping("/update-info-with-image")
    public String updateInfoWithImage(
            @RequestParam("aid") int aid,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "age", required = false) Integer age,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) {
        logger.info("接收到用户信息更新请求(带图片): aid={}, name={}, sex={}", aid, name, sex);
        
        try {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setAid(aid);
            accountInfo.setName(name);
            accountInfo.setText(text);
            
            // 设置年龄（如果提供）
            if (age != null) {
                accountInfo.setAge(age);
            }
            
            // 使用改进后的性别转换逻辑
            if (sex != null) {
                SexEnum sexEnum = SexEnum.fromString(sex);
                if (sexEnum != null) {
                    accountInfo.setSex(sexEnum);
                } else {
                    logger.warn("无效的性别值: {}", sex);
                    return RestBean.failure(400, "无效的性别值，请使用'男'/'女'或'MALE'/'FEMALE'").asJsonString();
                }
            }
            
            return infoService.infoUpdateWithImage(accountInfo, imageFile);
        } catch (Exception e) {
            logger.error("处理更新请求时出错", e);
            return RestBean.failure(500, "更新失败: " + e.getMessage()).asJsonString();
        }
    }
    
    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(MultipartException e) {
        logger.error("文件上传异常", e);
        return RestBean.failure(400, "文件上传请求处理失败: " + e.getMessage()).asJsonString();
    }
}
