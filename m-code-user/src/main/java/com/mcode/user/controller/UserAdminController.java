package com.mcode.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcode.common.exception.BusinessException;
import com.mcode.common.result.Result;
import com.mcode.user.dto.UserPageQueryDTO;
import com.mcode.user.dto.UserUpdateDTO;
import com.mcode.user.entity.User;
import com.mcode.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    private void checkAdmin(String role) {
        if (!"1".equals(role)) {
            throw new BusinessException("无管理员权限");
        }
    }

    @GetMapping("/users")
    public Result<Page<User>> list(@RequestHeader("X-User-Role") String role,
                                    UserPageQueryDTO dto) {
        checkAdmin(role);
        return Result.ok(userService.pageUsers(dto));
    }

    @GetMapping("/users/{id}")
    public Result<User> get(@RequestHeader("X-User-Role") String role,
                             @PathVariable Long id) {
        checkAdmin(role);
        return Result.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public Result<Void> update(@RequestHeader("X-User-Role") String role,
                                @PathVariable Long id,
                                @RequestBody UserUpdateDTO dto) {
        checkAdmin(role);
        userService.adminUpdateUser(id, dto);
        return Result.ok();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@RequestHeader("X-User-Role") String role,
                                @PathVariable Long id) {
        checkAdmin(role);
        userService.deleteUser(id);
        return Result.ok();
    }
}
