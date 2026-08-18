package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.ReportQueryDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 工作台服务实现类
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    //订单数据访问对象
    @Autowired
    private OrderMapper orderMapper;
    //用户数据访问对象
    @Autowired
    private UserMapper userMapper;
    //菜品数据访问对象
    @Autowired
    private DishMapper dishMapper;
    //套餐数据访问对象
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间区间统计营业数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业数据
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        //先统计全部订单，再复用同一查询条件限定已完成订单，避免统计口径不一致
        ReportQueryDTO orderCondition = ReportQueryDTO.builder().begin(begin).end(end).build();
        Integer totalOrderCount = orderMapper.countByMap(orderCondition);

        orderCondition.setStatus(Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(orderCondition);
        Integer validOrderCount = orderMapper.countByMap(orderCondition);

        //数据库聚合为空时按零处理，保证报表中的数值字段始终可计算
        double normalizedTurnover = turnover == null ? 0.0 : turnover;
        double orderCompletionRate = totalOrderCount == 0 ? 0.0
                : validOrderCount.doubleValue() / totalOrderCount;
        double unitPrice = validOrderCount == 0 ? 0.0 : normalizedTurnover / validOrderCount;

        Integer newUsers = userMapper.countByMap(DataOverViewQueryDTO.builder()
                .begin(begin).end(end).build());
        return BusinessDataVO.builder()
                .turnover(normalizedTurnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询当日订单管理数据
     * @return 订单概览数据
     */
    public OrderOverViewVO getOrderOverView() {
        LocalDateTime now = LocalDateTime.now();
        ReportQueryDTO condition = ReportQueryDTO.builder()
                .begin(now.with(LocalTime.MIN))
                .end(now.with(LocalTime.MAX))
                .build();

        condition.setStatus(Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(condition);
        condition.setStatus(Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(condition);
        condition.setStatus(Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(condition);
        condition.setStatus(Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(condition);
        condition.setStatus(null);
        Integer allOrders = orderMapper.countByMap(condition);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览数据
     * @return 菜品概览数据
     */
    public DishOverViewVO getDishOverView() {
        DishPageQueryDTO condition = new DishPageQueryDTO();
        condition.setStatus(StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(condition);
        condition.setStatus(StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(condition);
        return DishOverViewVO.builder().sold(sold).discontinued(discontinued).build();
    }

    /**
     * 查询套餐总览数据
     * @return 套餐概览数据
     */
    public SetmealOverViewVO getSetmealOverView() {
        SetmealPageQueryDTO condition = new SetmealPageQueryDTO();
        condition.setStatus(StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(condition);
        condition.setStatus(StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(condition);
        return SetmealOverViewVO.builder().sold(sold).discontinued(discontinued).build();
    }
}
