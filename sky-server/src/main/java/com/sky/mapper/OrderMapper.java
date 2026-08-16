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

    Orders getById(Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    Integer countStatus(Integer status);

    void update(Orders orders);
}
