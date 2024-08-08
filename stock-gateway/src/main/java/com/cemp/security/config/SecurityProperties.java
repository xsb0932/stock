package com.cemp.security.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "cemp.security")
@Validated
@Data
public class SecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     */
    @NotEmpty(message = "Token Header 不能为空")
    private String tokenHeader = "Authorization";

    /**
     * token 过期时间，默认30天
     */
    private Long tokenTimeOut = 30 * 2 * 24 * 30L;

    /**
     * 免登录的 URL 列表
     */
    private List<String> permitAllUrls = List.of("/**");

}
