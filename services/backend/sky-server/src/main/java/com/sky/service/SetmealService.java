package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 定义套餐业务对外提供的操作
 */
public interface SetmealService {

    /**
     * 新增套餐及关联菜品
     * @param setmealDTO 套餐请求参数
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO 套餐分页查询条件
     * @return业务处理结果
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids 业务对象主键集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 查询套餐及关联菜品
     * @param id 业务对象主键
     * @return业务处理结果
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐及关联菜品
     * @param setmealDTO 套餐请求参数
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 启售或停售套餐
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal 套餐对象
     * @return业务处理结果
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id 业务对象主键
     * @return业务处理结果
     */
    List<DishItemVO> getDishItemById(Long id);
}
