package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDataVO implements Serializable {

    //营业额
    private Double turnover;//营业额

    //有效订单数
    private Integer validOrderCount;//有效订单数

    //订单完成率
    private Double orderCompletionRate;//订单完成率

    //商品单价
    private Double unitPrice;//平均客单价

    //新增用户数
    private Integer newUsers;//新增用户数

}
