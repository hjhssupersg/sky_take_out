package com.sky.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定并提供微信登录与支付相关配置项
 */
@Component
@ConfigurationProperties(prefix = "sky.wechat")
@Data
public class WeChatProperties {

    //微信小程序应用ID
    private String appid; //小程序的appid
    //微信小程序应用密钥
    private String secret; //小程序的秘钥
    //微信支付商户号
    private String mchid; //商户号
    //微信支付商户证书序列号
    private String mchSerialNo; //商户API证书的证书序列号
    //微信支付商户私钥文件路径
    private String privateKeyFilePath; //商户私钥文件
    //微信支付APIv3密钥
    private String apiV3Key; //证书解密的密钥
    //微信支付平台证书文件路径
    private String weChatPayCertFilePath; //平台证书
    //微信支付结果通知地址
    private String notifyUrl; //支付成功的回调地址
    //微信退款结果通知地址
    private String refundNotifyUrl; //退款成功的回调地址

}
