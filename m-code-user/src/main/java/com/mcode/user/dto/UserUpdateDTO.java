package com.mcode.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer status;
    private Integer role;
}
