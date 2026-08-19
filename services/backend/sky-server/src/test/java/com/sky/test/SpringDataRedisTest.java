package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 验证Redis数据访问相关功能是否可用
 */
@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    /**
     * Redis数据访问模板
     */
    //Redis数据访问模板
    private RedisTemplate redisTemplate;

}
