package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 负责接收并处理管理端套餐相关HTTP请求
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    //套餐业务服务
    private SetmealService setmealService;

    /**
     * 新增套餐及其关联菜品
     * @param setmealDTO 套餐请求参数
     * @return业务处理结果
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param queryDTO 查询条件
     * @return业务处理结果
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO queryDTO) {
        log.info("套餐分页查询：{}", queryDTO);
        return Result.success(setmealService.pageQuery(queryDTO));
    }

    /**
     * 批量删除套餐
     * @param ids 业务对象主键集合
     * @return业务处理结果
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐详情及其关联菜品
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        return Result.success(setmealService.getByIdWithDish(id));
    }

    /**
     * 修改套餐及其关联菜品
     * @param setmealDTO 套餐请求参数
     * @return业务处理结果
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 启售或停售套餐
     * @param status 业务状态编码
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售或停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        log.info("套餐起售或停售：status={}, id={}", status, id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}
