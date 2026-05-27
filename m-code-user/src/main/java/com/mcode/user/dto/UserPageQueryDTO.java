package com.mcode.user.dto;

import lombok.Data;

@Data
public class UserPageQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String keyword;
    private Integer status;
    private Integer role;
}
