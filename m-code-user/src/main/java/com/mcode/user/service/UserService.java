package com.mcode.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.user.dto.LoginDTO;
import com.mcode.user.dto.RegisterDTO;
import com.mcode.user.dto.UserPageQueryDTO;
import com.mcode.user.dto.UserUpdateDTO;
import com.mcode.user.entity.User;

public interface UserService {

    String register(RegisterDTO dto);

    String login(LoginDTO dto);

    User getProfile(Long userId);

    void updateProfile(Long userId, User user);

    Page<User> pageUsers(UserPageQueryDTO dto);

    User getUserById(Long userId);

    void adminUpdateUser(Long userId, UserUpdateDTO dto);

    void deleteUser(Long userId);
}
