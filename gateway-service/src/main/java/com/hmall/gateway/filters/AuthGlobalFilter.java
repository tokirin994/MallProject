package com.hmall.gateway.filters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;

import cn.hutool.core.text.AntPathMatcher;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@EnableConfigurationProperties(AuthProperties.class)
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered{

    
    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求
        ServerHttpRequest request = exchange.getRequest();
        // 2. 判断是否拦截
        if (isExclude(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        // 3. 获取header
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null && headers.size() > 0) {
            token = headers.get(0);
        }

        // 4. 解析
        Long userId = null;

        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 5. 传递用户Id
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo))
                .build();
        
        // 6. 放行
        return chain.filter(swe);
    }


    public boolean isExclude(String path) {
        for (String pathPatten : authProperties.getExcludePaths()) {
            if(antPathMatcher.match(pathPatten, path)) {
                return true;
            }
        }

        return false;
    }

    

}
