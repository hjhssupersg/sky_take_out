package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("UserShopController")
@Slf4j
@RequestMapping("/user/shop")

/**
 * 店铺管理（用户端）
 */
public class ShopController {
    //店铺营业状态在Redis中的键名
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    //Redis数据访问模板
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺营业状态
     * @return业务处理结果
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取店铺营业状态");
        String statusValue = (String) redisTemplate.opsForValue().get(KEY);
        Integer status = statusValue == null ? null : Integer.valueOf(statusValue);
        return Result.success(status);
    }
}
