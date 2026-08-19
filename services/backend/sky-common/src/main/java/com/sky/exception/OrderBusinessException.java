package com.sky.exception;

/**
 * 表示订单处理过程中需要返回给调用方的业务异常
 */
public class OrderBusinessException extends BaseException {

    /**
     * 创建订单业务异常对象
     */
    public OrderBusinessException(String msg) {
        super(msg);
    }

}
