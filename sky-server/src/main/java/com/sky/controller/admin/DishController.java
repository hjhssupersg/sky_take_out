package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    //菜品业务服务
    private DishService dishService;
    @Autowired
    //Redis数据访问模板
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO 菜品请求参数
     * @return业务处理结果
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理受影响的分类下的菜品缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return业务处理结果
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids 业务对象主键集合
     * @return业务处理结果
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //只要批量删除菜品后，将所有缓存中的菜品删掉
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 起售、停售菜品
     * @param status 业务状态编码
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("起售、停售菜品：status={}, id={}", status, id);
        dishService.startOrStop(status, id);

        //只要起售或停售菜品后，将所有缓存中的菜品删掉
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @GetMapping("/{id}")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id) {
        log.info("根据id查询菜品信息和对应的口味信息：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品和对应的口味
     * @param dishDTO 菜品请求参数
     * @return业务处理结果
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //只要修改菜品后，清理所有菜品缓存数据
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类主键
     * @return业务处理结果
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        return Result.success(dishService.list(dish));
    }

    /**
     * 清理菜品缓存
     * @param pattern 缓存键模式
     */
    public void cleanCache(String pattern) {
        log.info("清理菜品缓存：{}", pattern);
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
