package com.sky.exception;

/**
 * 表示地址簿处理过程中需要返回给调用方的业务异常
 */
public class AddressBookBusinessException extends BaseException {

    /**
     * 创建地址簿业务异常对象
     */
    public AddressBookBusinessException(String msg) {
        super(msg);
    }

}
