package com.sky.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T> 统一响应数据的类型参数
 */
@Data
public class Result<T> implements Serializable {

    //响应状态码
    private Integer code; //编码：1成功，0和其它数字为失败
    //响应提示信息
    private String msg; //错误信息
    //响应业务数据
    private T data; //数据

    /**
     * 创建表示操作成功的统一响应结果
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    /**
     * 创建表示操作成功的统一响应结果
     */
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    /**
     * 创建表示操作失败并携带提示信息的统一响应结果
     */
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
