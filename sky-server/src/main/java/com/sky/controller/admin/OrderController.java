package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 负责接收并处理管理端订单相关HTTP请求
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    //订单业务服务
    private OrderService orderService;

    /**
     * 条件查询订单
     * @param ordersPageQueryDTO 查询条件
     * @return 订单分页数据
     */
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("条件查询订单：{}", ordersPageQueryDTO);
        return Result.success(orderService.conditionSearch(ordersPageQueryDTO));
    }

    /**
     * 查询各状态订单数量
     * @return 订单统计数据
     */
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        log.info("查询订单统计数据");
        return Result.success(orderService.statistics());
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        return Result.success(orderService.details(id));
    }

    /**
     * 接单
     * @param ordersConfirmDTO 接单信息
     * @return 操作结果
     */
    @PutMapping("/confirm")
    public Result<Void> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("订单接单：{}", ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 拒单信息
     * @return 操作结果
     */
    @PutMapping("/rejection")
    public Result<Void> rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("订单拒单：{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 取消订单信息
     * @return 操作结果
     */
    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单：{}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id 订单id
     * @return 操作结果
     */
    @PutMapping("/delivery/{id}")
    public Result<Void> delivery(@PathVariable Long id) {
        log.info("派送订单：{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id 订单id
     * @return 操作结果
     */
    @PutMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        log.info("完成订单：{}", id);
        orderService.complete(id);
        return Result.success();
    }
}
