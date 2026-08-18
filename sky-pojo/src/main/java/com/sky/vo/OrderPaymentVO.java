package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 封装订单支付业务的接口返回数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentVO implements Serializable {

    //微信支付随机字符串
    private String nonceStr; //随机字符串
    //微信支付签名
    private String paySign; //签名
    //微信支付时间戳
    private String timeStamp; //时间戳
    //微信支付签名算法
    private String signType; //签名算法
    //微信支付调起参数
    private String packageStr; //统一下单接口返回的 prepay_id 参数值

}
