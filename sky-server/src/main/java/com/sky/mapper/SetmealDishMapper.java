package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 批量新增套餐菜品关联
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 查询套餐关联菜品
     * @param setmealId
     * @return
     */
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 删除套餐关联菜品
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);

    /**
     * 查询套餐内菜品状态
     * @param setmealId
     * @return
     */
    List<Integer> getDishStatusesBySetmealId(Long setmealId);
    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
