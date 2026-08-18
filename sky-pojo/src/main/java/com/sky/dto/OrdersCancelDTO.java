package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装订单取消业务的请求参数
 */
@Data
public class OrdersCancelDTO implements Serializable {

    //业务对象主键
    private Long id;
    //订单取消原因
    private String cancelReason;

}
