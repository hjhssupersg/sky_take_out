package com.sky.mapper;

import com.sky.entity.User;
import com.sky.dto.DataOverViewQueryDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据动态条件统计用户数量
     * @param dataOverViewQueryDTO 查询条件
     * @return 用户数量
     */
    Integer countByMap(DataOverViewQueryDTO dataOverViewQueryDTO);

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);
}
