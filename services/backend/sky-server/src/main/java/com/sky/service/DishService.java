package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 定义菜品业务对外提供的操作
 */
public interface DishService {
    /**
     * 新增菜品和对应的口味
     * @param dishDTO 菜品请求参数
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return业务处理结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids 业务对象主键集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 起售、停售菜品
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id 业务对象主键
     * @return业务处理结果
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品和对应的口味
     * @param dishDTO 菜品请求参数
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据条件查询菜品
     * @param dish 菜品对象
     * @return业务处理结果
     */
    List<Dish> list(Dish dish);

    /**
     * 条件查询菜品和口味
     * @param dish 菜品对象
     * @return业务处理结果
     */
    List<DishVO> listWithFlavor(Dish dish);
}
