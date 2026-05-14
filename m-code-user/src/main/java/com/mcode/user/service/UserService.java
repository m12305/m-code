package com.mcode.user.service;

import com.mcode.user.dto.LoginDTO;
import com.mcode.user.dto.RegisterDTO;
import com.mcode.user.entity.User;

public interface UserService {

    //用户注册
    String register(RegisterDTO dto);

    //用户登录
    String login(LoginDTO dto);

    //获取用户信息
    User getProfile(Long userId);

    //更新用户信息
    void updateProfile(Long userId, User user);
}
