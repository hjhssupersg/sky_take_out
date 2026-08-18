package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * 工作台服务接口
 */
public interface WorkspaceService {

    /**
     * 根据时间区间统计营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业数据
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询当日订单管理数据
     * @return 订单概览数据
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览数据
     * @return 菜品概览数据
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览数据
     * @return 套餐概览数据
     */
    SetmealOverViewVO getSetmealOverView();
}
