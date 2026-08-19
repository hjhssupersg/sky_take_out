package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 封装购物车业务的请求参数
 */
@Data
public class ShoppingCartDTO implements Serializable {

    //菜品主键
    private Long dishId;
    //套餐主键
    private Long setmealId;
    //菜品口味描述
    private String dishFlavor;

}
