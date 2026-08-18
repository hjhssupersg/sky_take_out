package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotion.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 负责员工数据的持久化访问
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 登录用户名
     * @return业务处理结果
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee 员工对象
     */
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status) " +
            "values(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    @AutoFill(value = OperationType.INSERT)
    /**
     * 新增对应业务数据记录
     */
    void insert(Employee employee);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO 员工分页查询条件
     * @return业务处理结果
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态修改员工属性
     * @param employee 员工对象
     * @return业务处理结果
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
