package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDate;

/**
 * 运营数据报表服务接口
 */
public interface ReportService {
    /**
     * 根据时间区间统计营业额
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);

    /**
     * 根据时间区间统计用户数量
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户统计结果
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 根据时间区间统计订单数量和完成率
     * @param begin 开始日期
     * @param end 结束日期
     * @return 订单统计结果
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询指定时间区间内销量排名前十的商品
     * @param begin 开始日期
     * @param end 结束日期
     * @return 销量排名统计结果
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出近 30 天的运营数据报表
     * @param response HTTP 响应
     */
    void exportBusinessData(HttpServletResponse response);
}
