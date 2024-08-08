package com.cemp.security.context;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cemp.security.config.Response;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * SecurityContextRepositoryImpl
 *
 * @author 张力方
 * @since 2023/5/31
 **/
@Slf4j
public class ServerSecurityContextRepositoryImpl implements ServerSecurityContextRepository {



    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        log.info("========start load ================");

//        boolean anyMatch = securityProperties.getPermitAllUrls()
//                .stream()
//                .anyMatch(it -> exchange.getRequest().getURI().getPath().contains(it));
//        if (anyMatch) {
//            return Mono.empty();
//        }

        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        response.setStatusCode(HttpStatus.OK);
        // 获取请求头中的token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isNotBlank(token)) {
            JSONObject obj = new JSONObject();
            obj.put("userid","anoy");
            obj.put("token",token);
            SecurityContext emptyContext = new SecurityContextImpl();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(obj, null, null);
            emptyContext.setAuthentication(authentication);
            return Mono.just(emptyContext);

        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONBytes(new Response("9999",false,"503","没有权限","没有权限")));
        response.writeWith(Mono.just(dataBuffer));
        return Mono.empty();
    }
}
