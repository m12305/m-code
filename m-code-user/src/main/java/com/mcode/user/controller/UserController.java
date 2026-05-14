package com.mcode.user.controller;

import com.mcode.common.result.Result;
import com.mcode.user.dto.LoginDTO;
import com.mcode.user.dto.RegisterDTO;
import com.mcode.user.entity.User;
import com.mcode.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO dto) {
        String token = userService.register(dto);
        return Result.ok("注册成功", token);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.ok("登录成功", token);
    }

    @GetMapping("/profile")
    public Result<User> getProfile(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                       @RequestBody User user) {
        userService.updateProfile(userId, user);
        return Result.ok();
    }
}
