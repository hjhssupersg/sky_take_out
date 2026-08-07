package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /** 新增套餐及关联菜品 */
    void saveWithDish(SetmealDTO setmealDTO);

    /** 分页查询套餐 */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /** 批量删除套餐 */
    void deleteBatch(List<Long> ids);

    /** 查询套餐及关联菜品 */
    SetmealVO getByIdWithDish(Long id);

    /** 修改套餐及关联菜品 */
    void updateWithDish(SetmealDTO setmealDTO);

    /** 启售或停售套餐 */
    void startOrStop(Integer status, Long id);
}
