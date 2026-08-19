package com.sky.exception;

/**
 * 套餐启用失败异常
 */
public class SetmealEnableFailedException extends BaseException {

    /**
     * 创建套餐启用失败异常对象
     */
    public SetmealEnableFailedException(){}

    /**
     * 创建套餐启用失败异常对象
     */
    public SetmealEnableFailedException(String msg){
        super(msg);
    }
}
