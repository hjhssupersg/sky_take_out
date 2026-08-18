package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装员工密码修改业务的请求参数
 */
@Data
public class PasswordEditDTO implements Serializable {

    //员工id
    private Long empId;

    //旧密码
    private String oldPassword;

    //新密码
    private String newPassword;

}
