package com.sky.annotion;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记需要进行 “公共字段自动填充 ”的方法
 */
/**
 * 标记需要按数据库操作类型自动填充审计字段的Mapper方法
 */
@Target(ElementType.METHOD)//表示该注解可以用在方法上
@Retention(RetentionPolicy.RUNTIME)//表示该注解在运行时仍然可用，可以通过反射获取到该注解的信息
public @interface AutoFill {
    //数据库操作类型：UPDATE、INSERT
    /**
     * 声明自动填充注解对应的数据库操作类型
     */
    OperationType value();
}
