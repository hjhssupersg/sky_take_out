package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * 负责购物车数据的持久化访问
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     * @param shoppingCart 购物车对象
     * @return业务处理结果
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id更新商品数量
     * @param shoppingCart 购物车对象
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 添加商品到购物车
     * @param shoppingCart 购物车对象
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    /**
     * 新增对应业务数据记录
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车数据
     * @param userId 用户主键
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量添加购物车商品
     * @param shoppingCartList 购物车商品列表
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
