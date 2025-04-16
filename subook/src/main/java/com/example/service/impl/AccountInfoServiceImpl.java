package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AccountInfo;
import com.example.entity.dto.AccountInfoDTO;
import com.example.entity.vo.RestBean;
import com.example.mapper.AccountInfoMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Base64;

@Service
public class AccountInfoServiceImpl extends ServiceImpl<AccountInfoMapper, AccountInfo> implements AccountInfoService {
    private static final Logger logger = LoggerFactory.getLogger(AccountInfoServiceImpl.class);
    
    @Resource
    AccountInfoMapper accountInfoMapper;
    @Resource
    AccountDetailsService detailsService;
    
    public String info(String phoneOrEmail){
        try {
            AccountInfo accountInfo = this.query()
                    .eq("aid",
                            detailsService.query()
                            .select("id")
                            .eq("phone", phoneOrEmail).or()
                            .eq("email", phoneOrEmail)
                            .one().getId())
                    .one();
            if (accountInfo == null) return RestBean.failure(404,"用户信息不存在").asJsonString();
            
            // 转换为包含Base64图片的DTO
            AccountInfoDTO infoDTO = AccountInfoDTO.fromAccountInfo(accountInfo);
            return RestBean.success(infoDTO).asJsonString();
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return RestBean.failure(500, "获取用户信息失败: " + e.getMessage()).asJsonString();
        }
    }
    
    public String infoUpdate(AccountInfo accountInfo){
        try {
            logger.info("开始更新用户信息，aid={}, name={}, age={}, sex={}", 
                accountInfo.getAid(), accountInfo.getName(), accountInfo.getAge(), accountInfo.getSex());
            
            if (accountInfo.getAid() <= 0) {
                return RestBean.failure(400, "无效的用户ID").asJsonString();
            }
            
            // 使用LambdaUpdateWrapper直接指定要更新的字段
            LambdaUpdateWrapper<AccountInfo> updateWrapper = Wrappers.<AccountInfo>lambdaUpdate()
                    .eq(AccountInfo::getAid, accountInfo.getAid());
            
            // 只设置需要更新的非敏感字段
            boolean hasUpdates = false;
            
            if (accountInfo.getName() != null) {
                updateWrapper.set(AccountInfo::getName, accountInfo.getName());
                hasUpdates = true;
            }
            if (accountInfo.getSex() != null) {
                updateWrapper.set(AccountInfo::getSex, accountInfo.getSex());
                hasUpdates = true;
            }
            if (accountInfo.getAge() != null) {
                updateWrapper.set(AccountInfo::getAge, accountInfo.getAge());
                hasUpdates = true;
            }
            if (accountInfo.getText() != null) {
                updateWrapper.set(AccountInfo::getText, accountInfo.getText());
                hasUpdates = true;
            }
            
            if (!hasUpdates) {
                return RestBean.failure(400, "没有提供任何需要更新的字段").asJsonString();
            }
            
            logger.info("更新条件: aid={}", accountInfo.getAid());
            
            // 传入null作为实体，只使用更新包装器
            boolean updated = this.update(null, updateWrapper);
            
            if (updated) {
                return RestBean.success("更新成功").asJsonString();
            } else {
                return RestBean.failure(400, "修改失败，请检查用户ID是否正确").asJsonString();
            }
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            return RestBean.failure(500, "更新用户信息失败: " + e.getMessage()).asJsonString();
        }
    }
    
    public String infoUpdateWithImage(AccountInfo accountInfo, MultipartFile imageFile) {
        try {
            logger.info("开始更新用户信息(带图片)，aid={}, name={}", accountInfo.getAid(), accountInfo.getName());
            
            if (accountInfo.getAid() <= 0) {
                return RestBean.failure(400, "无效的用户ID").asJsonString();
            }
            
            // 处理图片
            byte[] imageBytes = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageBytes = imageFile.getBytes();
            }
            
            // 使用LambdaUpdateWrapper直接指定要更新的字段
            LambdaUpdateWrapper<AccountInfo> updateWrapper = Wrappers.<AccountInfo>lambdaUpdate()
                    .eq(AccountInfo::getAid, accountInfo.getAid());
            
            // 只设置需要更新的非敏感字段
            boolean hasUpdates = false;
            
            if (accountInfo.getName() != null) {
                updateWrapper.set(AccountInfo::getName, accountInfo.getName());
                hasUpdates = true;
            }
            if (accountInfo.getSex() != null) {
                updateWrapper.set(AccountInfo::getSex, accountInfo.getSex());
                hasUpdates = true;
            }
            if (accountInfo.getAge() != null) {
                updateWrapper.set(AccountInfo::getAge, accountInfo.getAge());
                hasUpdates = true;
            }
            if (accountInfo.getText() != null) {
                updateWrapper.set(AccountInfo::getText, accountInfo.getText());
                hasUpdates = true;
            }
            
            // 只有当图片存在时才设置
            if (imageBytes != null) {
                updateWrapper.set(AccountInfo::getImgData, imageBytes);
                hasUpdates = true;
            }
            
            if (!hasUpdates) {
                return RestBean.failure(400, "没有提供任何需要更新的字段").asJsonString();
            }
            
            // 传入null作为实体，只使用更新包装器
            boolean updated = this.update(null, updateWrapper);
            
            if (updated) {
                return RestBean.success("更新成功").asJsonString();
            } else {
                return RestBean.failure(400, "修改失败，请检查用户ID是否正确").asJsonString();
            }
        } catch (IOException e) {
            logger.error("处理图片失败", e);
            return RestBean.failure(500, "图片处理失败: " + e.getMessage()).asJsonString();
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            return RestBean.failure(500, "更新用户信息失败: " + e.getMessage()).asJsonString();
        }
    }
}
