package com.sky.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    /**
     * 创建账号被锁定异常对象
     */
    public AccountLockedException() {
    }

    /**
     * 创建账号被锁定异常对象
     */
    public AccountLockedException(String msg) {
        super(msg);
    }

}
