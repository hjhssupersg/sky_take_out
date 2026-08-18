package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import java.util.List;

/**
 * 定义分类业务对外提供的操作
 */
public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO 分类请求参数
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     * @param categoryPageQueryDTO 分类分页查询条件
     * @return业务处理结果
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     * @param id 业务对象主键
     */
    void deleteById(Long id);

    /**
     * 修改分类
     * @param categoryDTO 分类请求参数
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     * @param type 业务类型编码
     * @return业务处理结果
     */
    List<Category> list(Integer type);
}
