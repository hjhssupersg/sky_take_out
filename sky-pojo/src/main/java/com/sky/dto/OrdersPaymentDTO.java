package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 封装订单支付业务的请求参数
 */
@Data
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;

}
