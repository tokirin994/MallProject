package com.hmall.api.config;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.annotation.Bean;

import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.client.fallback.PayClientFallbackFactory;
import com.hmall.common.utils.UserContext;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoInterceptor(){
    
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    requestTemplate.header("user-info", userId.toString());
                }
            }
        }; 
    }

    @Bean
    public ItemClientFallbackFactory itemClientFallBackFactory(){
        return new ItemClientFallbackFactory();
    }
    
    @Bean
    public PayClientFallbackFactory payClientFallbackFactory(){
        return new PayClientFallbackFactory();
    }

}
