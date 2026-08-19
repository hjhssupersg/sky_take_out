package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotion.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 负责套餐数据的持久化访问
 */
@Mapper
public interface SetmealMapper {

    //根据分类id查询套餐的数量
    //@param id
    //@return
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    /**
     * 统计指定分类下的关联数据数量
     */
    Integer countByCategoryId(@Param("categoryId") Long id);

    /**
     * 新增套餐
     * @param setmeal 套餐对象
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO 套餐分页查询条件
     * @return业务处理结果
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据条件统计套餐数量
     * @param setmealPageQueryDTO 查询条件
     * @return 套餐数量
     */
    Integer countByMap(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据id删除套餐
     * @param id 业务对象主键
     */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);

    /**
     * 修改套餐
     * @param setmeal 套餐对象
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 动态条件查询套餐
     * @param setmeal 套餐对象
     * @return业务处理结果
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId 套餐主键
     * @return业务处理结果
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    /**
     * 查询套餐关联的菜品明细
     */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);


}
