package com.sky.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private AddressBookMapper addressBookMapper;
    @Autowired private ShoppingCartMapper shoppingCartMapper;

    @Override
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

    @Override
    @Transactional
    public void pay(OrdersPaymentDTO ordersPaymentDTO) {
        if (ordersPaymentDTO.getOrderNumber() == null || ordersPaymentDTO.getOrderNumber().trim().isEmpty()) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        int changed = orderMapper.markPaid(ordersPaymentDTO.getOrderNumber(), BaseContext.getCurrentId(), LocalDateTime.now());
        if (changed == 0) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        OrdersPageQueryDTO query = new OrdersPageQueryDTO();
        query.setUserId(BaseContext.getCurrentId());
        query.setStatus(status);
        Page<Orders> page = orderMapper.pageQuery(query);
        return new PageResult(page.getTotal(), toOrderVOList(page));
    }

    @Override
    public OrderVO details(Long id) {
        Orders orders = getRequiredOrder(id);
        return toOrderVO(orders);
    }

    @Override
    public OrderVO details4User(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!BaseContext.getCurrentId().equals(orders.getUserId())) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return toOrderVO(orders);
    }

    @Override
    @Transactional
    public void userCancelById(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!BaseContext.getCurrentId().equals(orders.getUserId())) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if (orders.getStatus() > Orders.TO_BE_CONFIRMED) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        cancelOrder(orders, "用户取消", null);
    }

    @Override
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

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO query) {
        PageHelper.startPage(query.getPage(), query.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(query);
        return new PageResult(page.getTotal(), toOrderVOList(page));
    }

    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO result = new OrderStatisticsVO();
        result.setToBeConfirmed(orderMapper.countStatus(Orders.TO_BE_CONFIRMED));
        result.setConfirmed(orderMapper.countStatus(Orders.CONFIRMED));
        result.setDeliveryInProgress(orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS));
        return result;
    }

    @Override @Transactional
    public void confirm(OrdersConfirmDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (!Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        updateStatus(orders.getId(), Orders.CONFIRMED);
    }

    @Override @Transactional
    public void rejection(OrdersRejectionDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (!Orders.TO_BE_CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        cancelOrder(orders, null, dto.getRejectionReason());
    }

    @Override @Transactional
    public void cancel(OrdersCancelDTO dto) {
        Orders orders = getRequiredOrder(dto.getId());
        if (Orders.CANCELLED.equals(orders.getStatus()) || Orders.COMPLETED.equals(orders.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        cancelOrder(orders, dto.getCancelReason(), null);
    }

    @Override @Transactional
    public void delivery(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!Orders.CONFIRMED.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        updateStatus(id, Orders.DELIVERY_IN_PROGRESS);
    }

    @Override @Transactional
    public void complete(Long id) {
        Orders orders = getRequiredOrder(id);
        if (!Orders.DELIVERY_IN_PROGRESS.equals(orders.getStatus())) throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(Orders.COMPLETED);
        update.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(update);
    }

    private List<OrderVO> toOrderVOList(List<Orders> orders) {
        return orders.stream().map(this::toOrderVO).collect(Collectors.toList());
    }

    private OrderVO toOrderVO(Orders orders) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> details = orderDetailMapper.getByOrderId(orders.getId());
        orderVO.setOrderDetailList(details);
        orderVO.setOrderDishes(details.stream().map(detail -> detail.getName() + "*" + detail.getNumber() + ";")
                .collect(Collectors.joining()));
        return orderVO;
    }

    private Orders getRequiredOrder(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        return orders;
    }

    private void updateStatus(Long id, Integer status) {
        Orders update = new Orders();
        update.setId(id);
        update.setStatus(status);
        orderMapper.update(update);
    }

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

    private String buildFullAddress(AddressBook addressBook) {
        return String.valueOf(addressBook.getProvinceName()) + String.valueOf(addressBook.getCityName())
                + String.valueOf(addressBook.getDistrictName()) + String.valueOf(addressBook.getDetail());
    }

}
