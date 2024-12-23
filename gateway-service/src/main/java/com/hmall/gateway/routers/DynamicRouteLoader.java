package com.hmall.gateway.routers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;

    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> ids = new HashSet<>();

    @PostConstruct
    public void initRoutrConfigListener() throws NacosException {
        // 1. 项目启动时候拉取配置，添加配置监听
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {

                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 2. 监听配置变更，更新路由表
                        updateConfigInfo(configInfo);

                    }

                });
        
        // 3. 第一次也要更新路由表
        updateConfigInfo(configInfo);

    }

    public void updateConfigInfo(String configInfo) {
        log.info("监听到路由信息");
        // 1. 解析配置信息，转换为RouteDefinition
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);

        // 2. 删除旧表，因为无法直接获得你要修改的那一个
        for (String id : ids) {
            routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        }
        
        // 3. 新增路由表并记录id供后面删除使用
        for (RouteDefinition routeDefinition : routeDefinitions) {
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            ids.add(routeDefinition.getId());
        }
    }
}
