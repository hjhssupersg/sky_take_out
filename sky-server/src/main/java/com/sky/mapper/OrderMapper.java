package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.ReportQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 负责订单数据的持久化访问
 */
@Mapper
public interface OrderMapper {
    /**
     * 根据动态条件统计营业额
     * @param reportQueryDTO 查询条件
     * @return 营业额合计
     */
    Double sumByMap(ReportQueryDTO reportQueryDTO);

    /**
     * 根据动态条件统计订单数量
     * @param reportQueryDTO 查询条件
     * @return 订单数量
     */
    Integer countByMap(ReportQueryDTO reportQueryDTO);

    /**
     * 查询指定时间区间内的销量排名前十商品
     * @param begin 开始时间
     * @param end 结束时间
     * @return 商品销量列表
     */
    List<GoodsSalesDTO> getSalesTop10(@Param("begin") LocalDateTime begin,
                                      @Param("end") LocalDateTime end);
    /**
     * 新增订单
     * @param orders 订单对象或订单列表
     */
    void insert(Orders orders);

    /**
     * 将当前用户的待付款订单标记为已支付
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
     * 根据订单号和用户查询订单
     *
     * @param orderNumber 订单号
     * @param userId 用户id
     * @return 订单信息
     */
    Orders getByNumberAndUserId(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    /**
     * 查询在指定时间之前、处于指定状态的订单
     *
     * @param status 订单状态
     * @param orderTime 下单时间上限
     * @return 符合条件的订单列表
     */
    List<Orders> getByStatusAndOrderTimeBefore(@Param("status") Integer status,
                                               @Param("orderTime") LocalDateTime orderTime);

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
