package com.mcode.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.user.dto.LoginDTO;
import com.mcode.user.dto.RegisterDTO;
import com.mcode.user.dto.UserPageQueryDTO;
import com.mcode.user.dto.UserUpdateDTO;
import com.mcode.user.entity.User;
import com.mcode.user.mapper.UserMapper;
import com.mcode.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Value("${jwt.secret:mcode-secret-key-2024-default-change-in-production}")
    private String secret;

    @Override
    public String register(RegisterDTO dto) {
        //判断用户是否已经存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        //插入操作
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setScore(0);
        user.setStatus(1);
        user.setRole(0);
        userMapper.insert(user);
        return generateToken(user);
    }

    @Override
    public String login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return generateToken(user);
    }

    @Override
    public User getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateProfile(Long userId, User updateUser) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        updateUser.setId(userId);
        updateUser.setPassword(null);
        updateUser.setUsername(null);
        userMapper.updateById(updateUser);
    }

    @Override
    public Page<User> pageUsers(UserPageQueryDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(dto.getStatus() != null, User::getStatus, dto.getStatus())
                .eq(dto.getRole() != null, User::getRole, dto.getRole())
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), w -> w
                        .like(User::getUsername, dto.getKeyword())
                        .or()
                        .like(User::getNickname, dto.getKeyword())
                        .or()
                        .like(User::getEmail, dto.getKeyword()))
                .orderByDesc(User::getCreateTime);
        Page<User> page = userMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void adminUpdateUser(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        User update = new User();
        update.setId(userId);
        update.setNickname(dto.getNickname());
        update.setAvatar(dto.getAvatar());
        update.setEmail(dto.getEmail());
        update.setPhone(dto.getPhone());
        update.setStatus(dto.getStatus());
        update.setRole(dto.getRole());
        userMapper.updateById(update);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(userId);
    }

    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("userId", user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000L))
                .signWith(key)
                .compact();
    }
}
