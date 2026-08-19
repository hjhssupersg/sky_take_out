package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 项目启动入口，负责加载Spring Boot应用上下文并启用事务、缓存和定时任务
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@MapperScan("com.sky.mapper")
@EnableTransactionManagement //开启注解方式的事务管理
@EnableCaching //开启缓存注解功能
@EnableScheduling //开启定时任务调度功能
@Slf4j
public class SkyApplication {
    /**
     * 启动Spring Boot应用并加载项目配置
     */
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
