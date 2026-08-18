package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 运营报表和工作台订单统计的查询条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportQueryDTO implements Serializable {

    //查询开始时间
    private LocalDateTime begin;

    //查询结束时间
    private LocalDateTime end;

    //订单状态
    private Integer status;
}
