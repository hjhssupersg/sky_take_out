package com.sky.dto;

import com.sky.entity.DishFlavor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装菜品业务的请求参数
 */
@Data
public class DishDTO implements Serializable {

    //业务对象主键
    private Long id;
    //菜品名称
    private String name;
    //菜品分类id
    private Long categoryId;
    //菜品价格
    private BigDecimal price;
    //图片
    private String image;
    //描述信息
    private String description;
    //0 停售 1 起售
    private Integer status;
    //口味
    //WebSocket会话集合，保存当前在线管理端连接
    private List<DishFlavor> flavors = new ArrayList<>();

}
