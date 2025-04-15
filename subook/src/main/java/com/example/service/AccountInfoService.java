package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.AccountInfo;

public interface AccountInfoService extends IService<AccountInfo> {
    String info(int aid);
}
