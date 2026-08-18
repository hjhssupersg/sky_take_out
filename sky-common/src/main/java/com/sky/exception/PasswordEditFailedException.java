package com.sky.exception;

/**
 * 密码修改失败异常
 */
public class PasswordEditFailedException extends BaseException{

    /**
     * 创建密码修改失败异常对象
     */
    public PasswordEditFailedException(String msg){
        super(msg);
    }

}
