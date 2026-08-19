package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * 定义用户业务对外提供的操作
 */
public interface UserService {
    /**
     * 用户微信登录
     * @param userLoginDTO 用户登录请求参数
     * @return业务处理结果
     */
    public User Wxlogin(UserLoginDTO userLoginDTO);
}
