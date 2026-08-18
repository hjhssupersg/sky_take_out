package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 封装用户登录业务的接口返回数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO implements Serializable {

    //业务对象主键
    private Long id;
    //微信用户唯一标识
    private String openid;
    //登录认证令牌
    private String token;

}
