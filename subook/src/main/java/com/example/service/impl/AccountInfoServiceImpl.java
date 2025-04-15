package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AccountInfo;
import com.example.entity.vo.RestBean;
import com.example.mapper.AccountInfoMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class AccountInfoServiceImpl extends ServiceImpl<AccountInfoMapper, AccountInfo> implements AccountInfoService {
    @Resource
    AccountInfoMapper accountInfoMapper;
    @Resource
    AccountDetailsService detailsService;
    public String info(String phoneOrEmail){
        AccountInfo accountInfo = this.query()
                .eq("aid",
                        detailsService.query()
                        .select("id")
                        .eq("phone", phoneOrEmail).or()
                        .eq("email", phoneOrEmail)
                        .one().getId())
                .one();
        if (accountInfo == null) return RestBean.failure(404,"用户信息不存在").asJsonString();
         else return RestBean.success(accountInfo).asJsonString();
    }
}
