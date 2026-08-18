package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 注册带有 {@code @ServerEndpoint} 注解的 WebSocket 服务端组件
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * 注册WebSocket服务端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
