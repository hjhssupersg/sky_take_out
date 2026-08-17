package com.sky.mapper;

import com.sky.entity.Orders;
import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 将当前用户的待付款订单标记为已支付。
     */
    int markPaid(@Param("orderNumber") String orderNumber,
                 @Param("userId") Long userId,
                 @Param("checkoutTime") LocalDateTime checkoutTime);

    /**
     * 根据id查询订单
     * @param id 订单id
     * @return 订单信息
     */
    Orders getById(Long id);

    /**
     * 条件分页查询订单
     * @param ordersPageQueryDTO 查询条件
     * @return 订单分页数据
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    Integer countStatus(Integer status);

    /**
     * 动态更新订单信息
     * @param orders 订单信息
     */
    void update(Orders orders);
}
