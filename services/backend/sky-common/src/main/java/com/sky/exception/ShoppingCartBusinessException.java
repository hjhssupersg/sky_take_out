package com.sky.exception;

/**
 * 表示购物车处理过程中需要返回给调用方的业务异常
 */
public class ShoppingCartBusinessException extends BaseException {

    /**
     * 创建购物车业务异常对象
     */
    public ShoppingCartBusinessException(String msg) {
        super(msg);
    }

}
