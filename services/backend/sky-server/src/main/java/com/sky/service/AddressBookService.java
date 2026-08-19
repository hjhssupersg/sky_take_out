package com.sky.service;

import com.sky.entity.AddressBook;
import java.util.List;

/**
 * 定义地址簿业务对外提供的操作
 */
public interface AddressBookService {

    /**
     * 条件查询
     * @param addressBook 地址簿对象
     * @return业务处理结果
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增地址
     * @param addressBook 地址簿对象
     */
    void save(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id 业务对象主键
     * @return业务处理结果
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook 地址簿对象
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook 地址簿对象
     */
    void setDefault(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id 业务对象主键
     */
    void deleteById(Long id);

}
