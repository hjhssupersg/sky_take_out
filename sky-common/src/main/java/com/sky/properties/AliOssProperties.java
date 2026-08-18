package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定并提供阿里云OSS文件存储相关配置项
 */
@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    //阿里云OSS访问节点
    private String endpoint;
    //阿里云OSS访问密钥ID
    private String accessKeyId;
    //阿里云OSS访问密钥
    private String accessKeySecret;
    //阿里云OSS存储桶名称
    private String bucketName;

}
