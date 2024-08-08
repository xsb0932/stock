package com.cemp.security.config;


import com.cemp.security.context.ServerSecurityContextRepositoryImpl;
import com.cemp.security.handle.AccessDeniedHandlerImpl;
import com.cemp.security.handle.AuthenticationEntryPointImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置类，主要用于相关组件的配置
 *
 * @author 张力方
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebFluxSecurity
public class CempSecurityConfiguration {

    @Bean
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public ServerSecurityContextRepository serverSecurityContextRepository() {
        return new ServerSecurityContextRepositoryImpl();
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {
//        String[] whiteAddress = new String[]{"/oauth/auth/login","/cemp-bms/test/test"};
        String[] whiteAddress = new String[]{"/**"};

        http
                // 开启跨域
//                .cors(Customizer.withDefaults())
                // CSRF 禁用，因为不使用 Session
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(serverSecurityContextRepository())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(serverAuthenticationEntryPoint())
                                .accessDeniedHandler(serverAccessDeniedHandler()))
                // 设置每个请求的权限
                // 全局共享规则
//                .authorizeExchange(authorizeHttpRequests -> authorizeHttpRequests
////                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()            //options放行
////                        .pathMatchers(whiteAddress).permitAll()             //全局白名单
////                        // 兜底规则，必须认证
////                        .anyExchange().authenticated())
                .authorizeExchange(authorizeHttpRequests -> authorizeHttpRequests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()            //options放行
                        .pathMatchers(whiteAddress).permitAll()             //全局白名单
                        // 兜底规则，必须认证
                        .anyExchange().authenticated())
        ;

        return http.build();
    }


}
