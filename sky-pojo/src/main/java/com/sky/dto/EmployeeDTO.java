package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装员工业务的请求参数
 */
@Data
public class EmployeeDTO implements Serializable {

    //业务对象主键
    private Long id;

    //登录用户名
    private String username;

    //业务对象名称
    private String name;

    //联系电话
    private String phone;

    //性别编码（0女，1男）
    private String sex;

    //身份证号码
    private String idNumber;

}
