package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotion.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 负责菜品数据的持久化访问
 */
@Mapper
public interface DishMapper {

    //根据分类id查询菜品数量
    //@param categoryId
    //@return
    @Select("select count(id) from dish where category_id = #{categoryId}")
    /**
     * 统计指定分类下的关联数据数量
     */
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @param dish 菜品对象
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return业务处理结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据条件统计菜品数量
     * @param dishPageQueryDTO 查询条件
     * @return 菜品数量
     */
    Integer countByMap(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品
     * @param id 业务对象主键
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id动态修改菜品数据
     * @param dish 菜品对象
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据条件查询菜品
     * @param dish 菜品对象
     * @return业务处理结果
     */
    List<Dish> list(Dish dish);
}
