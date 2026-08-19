package com.sky.exception;

/**
 * 表示用户处理过程中需要返回给调用方的业务异常
 */
public class UserNotLoginException extends BaseException {

    /**
     * 创建用户未登录异常对象
     */
    public UserNotLoginException() {
    }

    /**
     * 创建用户未登录异常对象
     */
    public UserNotLoginException(String msg) {
        super(msg);
    }

}
