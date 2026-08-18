package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装订单拒单业务的请求参数
 */
@Data
public class OrdersRejectionDTO implements Serializable {

    //业务对象主键
    private Long id;

    //订单拒绝原因
    private String rejectionReason;

}
