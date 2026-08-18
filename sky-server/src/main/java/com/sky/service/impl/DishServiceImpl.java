package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现菜品业务规则、数据校验及持久化协调
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    //菜品数据访问对象
    private DishMapper dishMapper;
    @Autowired
    //菜品口味数据访问对象
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    //套餐菜品关联数据访问对象
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO 菜品请求参数
     */
    /**
     * 保存菜品基础信息并同步维护菜品口味
     */
    @Transactional//开启事务管理
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值（新增菜品的id）
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            //向菜品口味表插入多条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return业务处理结果
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids 业务对象主键集合
     */
    public void deleteBatch(List<Long> ids) {
        //判断当前套餐能否被删除-是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品能否被删除-是否被套餐关联了
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
        }

        //删除菜品关联的口味数据
        for (Long id : ids) {
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 起售、停售菜品
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id 业务对象主键
     * @return业务处理结果
     */
    public DishVO getByIdWithFlavor(Long id) {
        //根据菜品id查询菜品信息
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //将查询到的数据封装到DishVO对象
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品和对应的口味
     * @param dishDTO 菜品请求参数
     */
     public void updateWithFlavor(DishDTO dishDTO) {
         //修改菜品表基本信息
         Dish dish = new Dish();
         BeanUtils.copyProperties(dishDTO, dish);
         dishMapper.update(dish);

        //删除原有口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

         //重新插入口味数据
         List<DishFlavor> flavors = dishDTO.getFlavors();
         if (flavors != null && !flavors.isEmpty()) {
             for (DishFlavor flavor : flavors) {
                 flavor.setDishId(dishDTO.getId());
             }
             //向菜品口味表插入多条数据
             dishFlavorMapper.insertBatch(flavors);
         }

     }
    /**
     * 根据条件查询菜品
     * @param dish 菜品对象
     * @return业务处理结果
     */
    public List<Dish> list(Dish dish) {
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish 菜品对象
     * @return业务处理结果
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
