package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 映射并承载员工相关业务数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {

    //序列化版本标识
    private static final long serialVersionUID = 1L;

    //业务对象主键
    private Long id;

    //登录用户名
    private String username;

    //业务对象名称
    private String name;

    //登录密码
    private String password;

    //联系电话
    private String phone;

    //性别编码（0女，1男）
    private String sex;

    //身份证号码
    private String idNumber;

    //业务状态编码
    private Integer status;

    //时间字段按yyyy-MM-dd HH:mm:ss格式序列化
    private LocalDateTime createTime;

    //时间字段按yyyy-MM-dd HH:mm:ss格式序列化
    private LocalDateTime updateTime;

    //创建人ID
    private Long createUser;

    //最后修改人ID
    private Long updateUser;

}
