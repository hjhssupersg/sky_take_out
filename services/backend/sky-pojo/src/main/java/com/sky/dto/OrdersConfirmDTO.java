package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装订单接单业务的请求参数
 */
@Data
public class OrdersConfirmDTO implements Serializable {

    //业务对象主键
    private Long id;
    //订单状态 1待付款 2待接单 3 已接单 4 派送中 5 已完成 6 已取消 7 退款
    private Integer status;

}
