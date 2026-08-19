package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 负责套餐菜品关联数据的持久化访问
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 批量新增套餐菜品关联
     * @param setmealDishes 套餐菜品关联列表
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 查询套餐关联菜品
     * @param setmealId 套餐主键
     * @return业务处理结果
     */
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 删除套餐关联菜品
     * @param setmealId 套餐主键
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 查询套餐内菜品状态
     * @param setmealId 套餐主键
     * @return业务处理结果
     */
    List<Integer> getDishStatusesBySetmealId(Long setmealId);
    /**
     * 根据菜品id查询套餐id
     * @param dishIds 菜品主键集合
     * @return业务处理结果
     */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
