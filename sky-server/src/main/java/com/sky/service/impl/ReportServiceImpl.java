package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.dto.ReportQueryDTO;
import com.sky.dto.DataOverViewQueryDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import com.sky.dto.GoodsSalesDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 运营数据报表服务实现
 */
@Service
public class ReportServiceImpl implements ReportService {
    //订单数据访问对象
    @Autowired private OrderMapper orderMapper;
    //用户数据访问对象
    @Autowired private UserMapper userMapper;
    //工作台业务服务
    @Autowired private WorkspaceService workspaceService;

    /**
     * 根据时间区间统计已完成订单的每日营业额
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计结果
     */
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = dateRange(begin, end);
        List<Double> values = new ArrayList<>();
        //按天构造查询条件，只累计已完成订单的金额，确保营业额与订单完成口径一致
        for (LocalDate date : dates) {
            ReportQueryDTO condition = dayCondition(date);
            condition.setStatus(Orders.COMPLETED);
            Double value = orderMapper.sumByMap(condition);
            values.add(value == null ? 0.0 : value);
        }
        return TurnoverReportVO.builder().dateList(join(dates)).turnoverList(StringUtils.join(values, ",")).build();
    }

    /**
     * 根据时间区间统计每日新增用户和累计用户数
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户统计结果
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = dateRange(begin, end);
        List<Integer> newUsers = new ArrayList<>();
        List<Integer> totalUsers = new ArrayList<>();
        for (LocalDate date : dates) {
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
            newUsers.add(userMapper.countByMap(userCondition(date)));
            DataOverViewQueryDTO totalCondition = DataOverViewQueryDTO.builder().end(dayEnd).build();
            totalUsers.add(userMapper.countByMap(totalCondition));
        }
        return UserReportVO.builder().dateList(join(dates)).newUserList(StringUtils.join(newUsers, ","))
                .totalUserList(StringUtils.join(totalUsers, ",")).build();
    }

    /**
     * 根据时间区间统计每日订单数、有效订单数和订单完成率
     * @param begin 开始日期
     * @param end 结束日期
     * @return 订单统计结果
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = dateRange(begin, end);
        List<Integer> all = new ArrayList<>();
        List<Integer> valid = new ArrayList<>();
        for (LocalDate date : dates) {
            ReportQueryDTO condition = dayCondition(date);
            all.add(orderMapper.countByMap(condition));
            condition.setStatus(Orders.COMPLETED);
            valid.add(orderMapper.countByMap(condition));
        }
        int total = all.stream().mapToInt(Integer::intValue).sum();
        int validTotal = valid.stream().mapToInt(Integer::intValue).sum();
        double rate = total == 0 ? 0.0 : (double) validTotal / total;
        return OrderReportVO.builder().dateList(join(dates)).orderCountList(StringUtils.join(all, ","))
                .validOrderCountList(StringUtils.join(valid, ",")).totalOrderCount(total)
                .validOrderCount(validTotal).orderCompletionRate(rate).build();
    }

    /**
     * 查询指定时间区间内商品销量排名前十的数据
     * @param begin 开始日期
     * @param end 结束日期
     * @return 销量排名统计结果
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> sales = orderMapper.getSalesTop10(begin.atStartOfDay(), end.atTime(LocalTime.MAX));
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(sales.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ","))
                .numberList(StringUtils.join(sales.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ","))
                .build();
    }

    /**
     * 导出近 30 天的运营数据报表
     * @param response HTTP 响应
     */
    public void exportBusinessData(HttpServletResponse response) {
        //报表覆盖昨天及之前的连续 30 天，避免将尚未结束的当天统计结果导出
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO overview = workspaceService.getBusinessData(
                begin.atStartOfDay(), end.atTime(LocalTime.MAX));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=business-data.xlsx");

        //以预置模板为基础填充数据，保留模板中的样式和固定单元格布局
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx")) {
            if (inputStream == null) {
                throw new IllegalStateException("运营数据报表模板不存在");
            }
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                 ServletOutputStream outputStream = response.getOutputStream()) {
                XSSFSheet sheet = workbook.getSheet("Sheet1");
                fillOverview(sheet, begin, end, overview);
                fillDailyDetails(sheet, begin);
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException("导出运营数据报表失败", e);
        }
    }

    /**
     * 填充报表概览区域
     * @param sheet 报表工作表
     * @param begin 统计开始日期
     * @param end 统计结束日期
     * @param businessData 营业数据
     */
    private void fillOverview(XSSFSheet sheet, LocalDate begin, LocalDate end, BusinessDataVO businessData) {
        sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + "至" + end);
        XSSFRow row = sheet.getRow(3);
        row.getCell(2).setCellValue(businessData.getTurnover());
        row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessData.getNewUsers());
        row = sheet.getRow(4);
        row.getCell(2).setCellValue(businessData.getValidOrderCount());
        row.getCell(4).setCellValue(businessData.getUnitPrice());
    }

    /**
     * 填充近 30 天的每日明细数据
     * @param sheet 报表工作表
     * @param begin 统计开始日期
     */
    private void fillDailyDetails(XSSFSheet sheet, LocalDate begin) {
        //明细区每一行对应一个自然日，行号从模板中的第 8 行开始
        for (int i = 0; i < 30; i++) {
            LocalDate date = begin.plusDays(i);
            BusinessDataVO businessData = workspaceService.getBusinessData(
                    date.atStartOfDay(), date.atTime(LocalTime.MAX));
            XSSFRow row = sheet.getRow(7 + i);
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(3).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData.getUnitPrice());
            row.getCell(6).setCellValue(businessData.getNewUsers());
        }
    }

    /**
     * 生成包含开始日期和结束日期的连续日期列表
     * @param begin 开始日期
     * @param end 结束日期
     * @return 日期列表
     */
    private List<LocalDate> dateRange(LocalDate begin, LocalDate end) {
        if (begin == null || end == null || begin.isAfter(end)) {
            throw new IllegalArgumentException("统计开始日期不能晚于结束日期，且日期不能为空");
        }
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) dates.add(date);
        return dates;
    }

    /**
     * 构造指定日期的开始和结束时间查询条件
     * @param date 统计日期
     * @return 日期范围查询条件
     */
    private ReportQueryDTO dayCondition(LocalDate date) {
        return ReportQueryDTO.builder()
                .begin(date.atStartOfDay())
                .end(date.atTime(LocalTime.MAX))
                .build();
    }

    /**
     * 构造指定日期的用户统计查询条件
     * @param date 统计日期
     * @return 用户统计查询条件
     */
    private DataOverViewQueryDTO userCondition(LocalDate date) {
        return DataOverViewQueryDTO.builder()
                .begin(date.atStartOfDay())
                .end(date.atTime(LocalTime.MAX))
                .build();
    }

    /**
     * 将日期列表转换为逗号分隔的字符串
     * @param dates 日期列表
     * @return 逗号分隔的日期字符串
     */
    private String join(List<LocalDate> dates) {
        return StringUtils.join(dates, ",");
    }
}
