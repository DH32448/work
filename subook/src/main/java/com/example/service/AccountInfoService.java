package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.AccountInfo;
import org.springframework.web.multipart.MultipartFile;

public interface AccountInfoService extends IService<AccountInfo> {
    String info(String phoneOrEmail);
    public String infoUpdate(AccountInfo accountInfo);
    String infoUpdateWithImage(AccountInfo accountInfo, MultipartFile imageFile);
}
