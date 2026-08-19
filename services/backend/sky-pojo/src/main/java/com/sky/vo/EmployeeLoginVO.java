package com.sky.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 封装员工登录业务的接口返回数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "员工登录返回的数据格式")
public class EmployeeLoginVO implements Serializable {

    @ApiModelProperty("主键值")
    //业务对象主键
    private Long id;

    @ApiModelProperty("用户名")
    //返回给前端的用户名
    private String userName;

    @ApiModelProperty("姓名")
    //业务对象名称
    private String name;

    @ApiModelProperty("jwt令牌")
    //登录认证令牌
    private String token;

}
