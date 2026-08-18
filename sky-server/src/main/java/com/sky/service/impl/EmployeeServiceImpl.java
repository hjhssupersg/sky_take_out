package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实现员工业务规则、数据校验及持久化协调
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    //员工数据访问对象
    private EmployeeMapper employeeMapper;

    /**
     * 将性别字段进行统一处理
     * @param sex 性别编码
     * @return业务处理结果
     */
    private String normalizeSex(String sex) {
        if (sex == null) {
            return null;
        }

        String normalized = sex.trim();
        if ("1".equals(normalized) || "男".equals(normalized) || "male".equalsIgnoreCase(normalized) || "m".equalsIgnoreCase(normalized)) {
            return "男";
        }
        if ("2".equals(normalized) || "女".equals(normalized) || "female".equalsIgnoreCase(normalized) || "f".equalsIgnoreCase(normalized)) {
            return "女";
        }
        return normalized;
    }

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工登录请求参数
     * @return业务处理结果
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传来的密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 员工请求参数
     */
    public void save(EmployeeDTO employeeDTO) {
        //创建一个员工entity对象
        Employee employee = new Employee();
        //拷贝属性
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setSex(normalizeSex(employeeDTO.getSex()));
        //设置默认密码123456，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置状态
        employee.setStatus(StatusConstant.ENABLE);
        //调用mapper层的“新增员工”方法
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO 员工分页查询条件
     * @return业务处理结果
     */
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        List<Employee> employees = page.getResult();
        if (employees != null) {
            employees.forEach(employee -> employee.setSex(normalizeSex(employee.getSex())));
        }
        return new PageResult(page.getTotal(), employees);
    }

    /**
     * 启用、禁用员工账号
     *
     * @param status 业务状态编码
     * @param id 业务对象主键
     */
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id 业务对象主键
     * @return业务处理结果
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        employee.setSex(normalizeSex(employee.getSex()));
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO 员工请求参数
     * @return业务处理结果
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setSex(normalizeSex(employeeDTO.getSex()));
        employeeMapper.update(employee);
    }

}
