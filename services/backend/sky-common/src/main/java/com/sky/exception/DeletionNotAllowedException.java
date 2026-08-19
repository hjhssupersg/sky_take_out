package com.sky.exception;

/**
 * 表示DeletionNotAllowedException处理过程中需要返回给调用方的业务异常
 */
public class DeletionNotAllowedException extends BaseException {

    /**
     * 创建不允许删除异常对象
     */
    public DeletionNotAllowedException(String msg) {
        super(msg);
    }

}
