package com.sky.exception;

/**
 * 账号不存在异常
 */
public class AccountNotFoundException extends BaseException {

    /**
     * 创建账号不存在异常对象
     */
    public AccountNotFoundException() {
    }

    /**
     * 创建账号不存在异常对象
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
