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

    /**
     * 分页查询历史订单
     * @param page 页码
     * @param pageSize 每页记录数
     * @param status 订单状态
     * @return 订单分页数据
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        log.info("分页查询历史订单：page={}, pageSize={}, status={}", page, pageSize, status);
        return Result.success(orderService.pageQuery4User(page, pageSize, status));
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询用户订单详情：{}", id);
        return Result.success(orderService.details4User(id));
    }

    /**
     * 取消订单
     * @param id 订单id
     * @return 操作结果
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        log.info("用户取消订单：{}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id 订单id
     * @return 操作结果
     */
    @PostMapping("/repetition/{id}")
    public Result<Void> repetition(@PathVariable Long id) {
        log.info("用户再来一单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }
}
