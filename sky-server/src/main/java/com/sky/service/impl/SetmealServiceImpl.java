package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.BaseException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐及关联菜品
     * @param setmealDTO
     */
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        validateSetmeal(setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insert(setmeal);

        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insertBatch(dishes);
    }

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        for (Long id : ids) {
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    /**
     * 查询套餐及关联菜品
     * @param id
     * @return
     */
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> dishes = setmealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(dishes);
        return setmealVO;
    }

    /**
     * 修改套餐及关联菜品
     * @param setmealDTO
     */
    @Transactional
    public void updateWithDish(SetmealDTO setmealDTO) {
        validateSetmeal(setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setmealDTO.getId());
        }
        setmealDishMapper.insertBatch(dishes);
    }

    /**
     * 启售或停售套餐
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        if (status.equals(StatusConstant.ENABLE)) {
            List<Integer> dishStatuses = setmealDishMapper.getDishStatusesBySetmealId(id);
            if (dishStatuses.contains(StatusConstant.DISABLE)) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 校验套餐必填信息
     * @param setmealDTO
     */
    private void validateSetmeal(SetmealDTO setmealDTO) {
        if (setmealDTO.getName() == null || setmealDTO.getName().trim().isEmpty()
                || setmealDTO.getCategoryId() == null
                || setmealDTO.getPrice() == null
                || setmealDTO.getImage() == null || setmealDTO.getImage().trim().isEmpty()) {
            throw new BaseException(MessageConstant.SETMEAL_REQUIRED_FIELDS);
        }
        if (setmealDTO.getSetmealDishes() == null || setmealDTO.getSetmealDishes().isEmpty()) {
            throw new BaseException(MessageConstant.SETMEAL_DISHES_REQUIRED);
        }
    }
}
