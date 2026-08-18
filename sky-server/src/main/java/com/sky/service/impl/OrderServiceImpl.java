package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现订单业务规则、数据校验及持久化协调
 */
@Service
public class OrderServiceImpl implements OrderService {
    //订单数据访问对象
    @Autowired private OrderMapper orderMapper;
    //订单明细数据访问对象
    @Autowired private OrderDetailMapper orderDetailMapper;
    //地址簿数据访问对象
    @Autowired private AddressBookMapper addressBookMapper;
    //购物车数据访问对象
    @Autowired private ShoppingCartMapper shoppingCartMapper;
    //WebSocket消息推送服务
    @Autowired private WebSocketServer webSocketServer;

    /**
     * 提交订单并生成订单明细
     * @param ordersSubmitDTO 下单信息
     * @return 订单提交结果
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getByIdAndUserId(ordersSubmitDTO.getAddressBookId(), userId);
        if (addressBook == null) throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        ShoppingCart condition = new ShoppingCart();
        condition.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(condition);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        String fullAddress = buildFullAddress(addressBook);

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(fullAddress);
        orders.setUserId(userId);
        orderMapper.insert(orders);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail detail = new OrderDetail();
            BeanUtils.copyProperties(cart, detail);
            detail.setOrderId(orders.getId());
            orderDetails.add(detail);
        }
        orderDetailMapper.insertBatch(orderDetails);
        return OrderSubmitVO.builder().id(orders.getId()).orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount()).orderTime(orders.getOrderTime()).build();
    }

    /**
     * 完成本地模拟支付并清空购物车
     * @param ordersPaymentDTO 支付信息
     */
    @Transactional
    public void pay(OrdersPaymentDTO ordersPaymentDTO) {
        if (ordersPaymentDTO.getOrderNumber() == null || ordersPaymentDTO.getOrderNumber().trim().isEmpty()) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);

        int changed = orderMapper.markPaid(ordersPaymentDTO.getOrderNumber(), userId, LocalDateTime.now());
        if (changed == 0) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        shoppingCartMapper.deleteByUserId(userId);
        sendOrderNotification(1, orders.getId(), orders.getNumber());
    }

    /**
     * 分页查询当前用户历史订单
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return 订单分页数据
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        OrdersPageQueryDTO query = new OrdersPageQueryDTO();
        query.setUserId(BaseContext.getCurrentId());
        query.setStatus(status);
        Page<Orders> page = orderMapper.pageQuery(query);
        return new PageResult(page.getTotal(), toOrderVOList(page));
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    public OrderVO details(Long id) {
        Orders orders = getRequiredOrder(id);
        return toOrderVO(orders);
    }

    /**
     * 查询当前用户的订单详情
     * @param id 订单id
     * @return 订单详情
     */
    public OrderVO details4User(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!BaseContext.getCurrentId().equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return toOrderVO(orders);
    }

    /**
     * 取消当前用户订单
     * @param id 订单id
     */
    @Transactional
    public void userCancelById(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!BaseContext.getCurrentId().equals(orders.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        cancelOrder(orders, "用户取消", null);
    }

    /**
     * 将历史订单商品加入当前用户购物车
     * @param id 订单id
     */
    @Transactional
    public void repetition(Long id) {
        Orders orders = getRequiredOrder(id);
        Long userId = BaseContext.getCurrentId();
        if (!userId.equals(orders.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        List<ShoppingCart> carts = orderDetailMapper.getByOrderId(id).stream().map(detail -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(detail, cart, "id", "orderId");
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).collect(Collectors.toList());
        if (!carts.isEmpty()) shoppingCartMapper.insertBatch(carts);
    }

    /**
     * 用户催单，并向在线管理端推送通知
     * @param id 订单id
     */
    public void reminder(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!BaseContext.getCurrentId().equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (orders.getStatus() < Orders.TO_BE_CONFIRMED || orders.getStatus() > Orders.DELIVERY_IN_PROGRESS) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        sendOrderNotification(2, orders.getId(), orders.getNumber());
    }

    /**
     * 管理端条件分页查询订单
     * @param query 查询条件
     * @return 订单分页数据
     */
    public PageResult conditionSearch(OrdersPageQueryDTO query) {
        PageHelper.startPage(query.getPage(), query.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(query);
        return new PageResult(page.getTotal(), toOrderVOList(page));
    }

    /**
     * 统计待接单、待派送和派送中的订单数量
     * @return 订单统计数据
     */
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO result = new OrderStatisticsVO();
        result.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        result.setConfirmed(orderMapper.countStatus(Orders.CONFIRMED));
        result.setDeliveryInProgress(orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS));
        return result;
    }

    /**
     * 接单
     * @param dto 接单信息
     */
    @Transactional
    public void confirm(OrdersConfirmDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (!Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        updateStatus(orders.getId(), Orders.CONFIRMED);
    }

    /**
     * 拒单
     * @param dto 拒单信息
     */
    @Transactional
    public void rejection(OrdersRejectionDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (!Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        cancelOrder(orders, null, dto.getRejectionReason());
    }

    /**
     * 取消订单
     * @param dto 取消订单信息
     */
    @Transactional
    public void cancel(OrdersCancelDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (Orders.CANCELLED.equals(orders.getStatus()) || Orders.COMPLETED.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        cancelOrder(orders, dto.getCancelReason(), null);
    }

    /**
     * 派送订单
     * @param id 订单id
     */
    @Transactional
    public void delivery(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!Orders.CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        updateStatus(id, Orders.DELIVERY_IN_PROGRESS);
    }

    /**
     * 完成订单
     * @param id 订单id
     */
    @Transactional
    public void complete(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!Orders.DELIVERY_IN_PROGRESS.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(Orders.COMPLETED);
        update.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(update);
    }

    /**
     * 将订单列表转换为订单视图对象列表
     * @param orders 订单列表
     * @return 订单视图对象列表
     */
    private List<OrderVO> toOrderVOList(List<Orders> orders) {
        return orders.stream().map(this::toOrderVO).collect(Collectors.toList());
    }

    /**
     * 将订单转换为订单视图对象
     * @param orders 订单信息
     * @return 订单视图对象
     */
    private OrderVO toOrderVO(Orders orders) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> details = orderDetailMapper.getByOrderId(orders.getId());
        orderVO.setOrderDetailList(details);
        orderVO.setOrderDishes(details.stream().map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                .collect(Collectors.joining()));
        return orderVO;
    }

    /**
     * 查询指定订单，不存在时抛出业务异常
     * @param id 订单id
     * @return 订单信息
     */
    private Orders getRequiredOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        return orders;
    }

    /**
     * 更新订单状态
     * @param id 订单id
     * @param status 订单状态
     */
    private void updateStatus(Long id, Integer status) {
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(status);
        orderMapper.update(update);
    }

    /**
     * 取消订单并记录取消或拒单原因
     * @param orders 原订单信息
     * @param cancelReason 取消原因
     * @param rejectionReason 拒单原因
     */
    private void cancelOrder(Orders orders, String cancelReason, String rejectionReason) {
        Orders update = new Orders();
        update.setId(orders.getId());
        update.setStatus(Orders.CANCELLED);
        update.setCancelReason(cancelReason);
        update.setRejectionReason(rejectionReason);
        update.setCancelTime(LocalDateTime.now());
        if (Orders.PAID.equals(orders.getPayStatus())) update.setPayStatus(Orders.REFUND);
        orderMapper.update(update);
    }

    /**
     * 按前后端约定封装订单通知并广播给在线管理端
     */
    private void sendOrderNotification(int type, Long orderId, String orderNumber) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("orderId", orderId);
        message.put("content", "订单号：" + orderNumber);
        webSocketServer.sendToAllClient(JSON.toJSONString(message));
    }

    /**
     * 拼接地址簿中的完整地址
     * @param addressBook 地址簿信息
     * @return 完整地址
     */
    private String buildFullAddress(AddressBook addressBook) {
        return String.valueOf(addressBook.getProvinceName()) + String.valueOf(addressBook.getCityName())
                + String.valueOf(addressBook.getDistrictName()) + String.valueOf(addressBook.getDetail());
    }

}
