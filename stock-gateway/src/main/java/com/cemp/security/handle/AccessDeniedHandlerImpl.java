package com.cemp.security.handle;

import com.alibaba.fastjson2.JSON;
import com.cemp.security.config.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



/**
 * 访问一个需要认证的 URL 资源，已经认证（登录）但是没有权限的情况下，返回 {@link GlobalErrorCodeConstants#FORBIDDEN} 错误码。
 * <p>
 * 补充：Spring Security 通过 {@link ExceptionTranslationFilter#handleAccessDeniedException(HttpServletRequest, HttpServletResponse, FilterChain, AccessDeniedException)}方法，调用当前类
 *
 * @author 张力方
 */
@Slf4j
@SuppressWarnings("JavadocReference") // 忽略文档引用报错
public class AccessDeniedHandlerImpl implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        log.info("========start access handle================");

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        log.error("access forbidden path={}", exchange.getRequest().getPath());
//        log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", exchange.getRequest().getPath(),

        // 返回 403
        if (exchange.getRequest().getMethod().equals(HttpMethod.OPTIONS)) {
            response.setStatusCode(HttpStatusCode.valueOf(200));
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONBytes(new Response("9999",false,"503","没有权限","没有权限")));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
