package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时处理超时未支付和长期派送中的订单
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    //订单数据访问对象
    private final OrderMapper orderMapper;

    /**
     * 创建订单定时任务组件
     */
    public OrderTask(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 每分钟取消下单超过 15 分钟仍未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(15);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeBefore(Orders.PENDING_PAYMENT, deadline);
        for (Orders order : orders) {
            Orders update = new Orders();
            update.setId(order.getId());
            update.setStatus(Orders.CANCELLED);
            update.setCancelReason("支付超时，自动取消");
            update.setCancelTime(LocalDateTime.now());
            orderMapper.update(update);
        }
        if (!orders.isEmpty()) log.info("已自动取消 {} 个支付超时订单", orders.size());
    }

    /**
     * 每天凌晨 1 点完成派送中超过 1 小时的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(1);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeBefore(Orders.DELIVERY_IN_PROGRESS, deadline);
        for (Orders order : orders) {
            Orders update = new Orders();
            update.setId(order.getId());
            update.setStatus(Orders.COMPLETED);
            update.setDeliveryTime(LocalDateTime.now());
            orderMapper.update(update);
        }
        if (!orders.isEmpty()) log.info("已自动完成 {} 个长期派送中订单", orders.size());
    }
}
