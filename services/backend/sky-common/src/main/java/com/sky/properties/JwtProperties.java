package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定并提供JWT令牌相关配置项
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    //管理端员工生成jwt令牌相关配置
    private String adminSecretKey;
    //管理端令牌有效期
    private long adminTtl;
    //管理端令牌请求头名称
    private String adminTokenName;

    //用户端微信用户生成jwt令牌相关配置
    private String userSecretKey;
    //用户端令牌有效期
    private long userTtl;
    //用户端令牌请求头名称
    private String userTokenName;

}
