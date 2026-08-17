package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("AdminShopController")
@Slf4j
@RequestMapping("/admin/shop")

/**
 * 店铺管理（管理端）
 */
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status 1：营业中，0：打烊中
     * @return
     */
    @PostMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为：{}", status == 1 ? "营业中" : "打烊中");
        //将店铺营业状态存入Redis中，key为"SHOP_STATUS"，value为status
        redisTemplate.opsForValue().set(KEY, String.valueOf(status));
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取店铺营业状态");
        String statusValue = (String) redisTemplate.opsForValue().get(KEY);
        Integer status = statusValue == null ? null : Integer.valueOf(statusValue);
        return Result.success(status);
    }
}
