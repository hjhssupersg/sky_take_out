package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 完成本地模拟支付，不调用第三方支付平台。
     * @param ordersPaymentDTO 支付订单信息
     */
    void pay(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 分页查询当前用户历史订单
     * @param page 页码
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return 订单分页数据
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    OrderVO details(Long id);

    /**
     * 查询当前用户的订单详情
     * @param id 订单id
     * @return 订单详情
     */
    OrderVO details4User(Long id);

    /**
     * 取消当前用户订单
     * @param id 订单id
     */
    void userCancelById(Long id);

    /**
     * 将历史订单商品加入购物车
     * @param id 订单id
     */
    void repetition(Long id);

    /**
     * 管理端条件查询订单
     * @param ordersPageQueryDTO 查询条件
     * @return 订单分页数据
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 统计各状态订单数量
     * @return 订单统计数据
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     * @param ordersConfirmDTO 接单信息
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单信息
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO 取消订单信息
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id 订单id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);
}
