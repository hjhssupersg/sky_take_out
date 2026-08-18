package com.sky.exception;

/**
 * 登录失败
 */
public class LoginFailedException extends BaseException{
    /**
     * 创建登录失败异常对象
     */
    public LoginFailedException(String msg){
        super(msg);
    }
}
