package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 模拟支付：支付成功后直接将订单更新为待接单。
     */
    @PostMapping("/payment")
    public Result<Void> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("用户模拟支付订单：{}", ordersPaymentDTO.getOrderNumber());
        orderService.pay(ordersPaymentDTO);
        return Result.success();
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        return Result.success(orderService.pageQuery4User(page, pageSize, status));
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        return Result.success(orderService.details4User(id));
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result<Void> repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
}
