package com.sky.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 封装订单分页查询的分页筛选条件
 */
@Data
public class OrdersPageQueryDTO implements Serializable {

    //页码
    private int page;

    //每页记录数
    private int pageSize;

    //订单号
    private String number;

    //联系电话
    private  String phone;

    //业务状态编码
    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //查询开始时间
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //查询结束时间
    private LocalDateTime endTime;

    //用户主键
    private Long userId;

}
