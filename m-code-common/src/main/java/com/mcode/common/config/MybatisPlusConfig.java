package com.mcode.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开启 MyBatis-Plus 分页功能
 */

@Configuration
public class MybatisPlusConfig {

    // 把分页拦截器交给 Spring 管理
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 拦截器（核心插件载体）
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加【MySQL 分页拦截器】
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
