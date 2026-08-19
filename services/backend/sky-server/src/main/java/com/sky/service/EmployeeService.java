package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

/**
 * 定义员工业务对外提供的操作
 */
public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 员工登录请求参数
     * @return业务处理结果
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 员工请求参数
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO 员工分页查询条件
     * @return业务处理结果
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用、禁用员工账号
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id 业务对象主键
     * @return业务处理结果
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO 员工请求参数
     * @return业务处理结果
     */
    void update(EmployeeDTO employeeDTO);
}
