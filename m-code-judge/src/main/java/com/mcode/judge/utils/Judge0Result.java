package com.mcode.judge.utils;

import lombok.Data;

@Data
public class Judge0Result {
    // 状态ID：3=AC，4=WA，5=TLE，6=MLE，7=CE
    private Integer statusId;
    // 状态描述：Accepted / Wrong Answer / Time Limit Exceeded
    private String statusDesc;
    // 程序标准输出
    private String stdout;
    // 程序错误输出
    private String stderr;
    // 编译错误信息
    private String compileOutput;
    // 额外信息
    private String message;
    // 运行时间（秒，字符串格式，如 "0.001"）
    private String time;
    // 占用内存（KB，数字）
    private Integer memory;
    // 本次判题token
    private String token;
}
