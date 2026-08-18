package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 负责地址簿数据的持久化访问
 */
@Mapper
public interface AddressBookMapper {

    /**
     * 条件查询
     * @param addressBook 地址簿对象
     * @return业务处理结果
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     * @param addressBook 地址簿对象
     */
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    /**
     * 新增对应业务数据记录
     */
    void insert(AddressBook addressBook);

    /**
     * 根据id查询
     * @param id 业务对象主键
     * @return业务处理结果
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据地址id和用户id查询地址
     * @param id 地址id
     * @param userId 用户id
     * @return 地址信息
     */
    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    AddressBook getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据id修改
     * @param addressBook 地址簿对象
     */
    void update(AddressBook addressBook);

    /**
     * 根据 用户id修改 是否默认地址
     * @param addressBook 地址簿对象
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id 业务对象主键
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

}
