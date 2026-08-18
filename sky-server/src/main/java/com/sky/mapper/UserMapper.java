package com.sky.mapper;

import com.sky.entity.User;
import com.sky.dto.DataOverViewQueryDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 负责用户数据的持久化访问
 */
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
     * @param openid 微信用户唯一标识
     * @return业务处理结果
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user 用户对象
     */
    void insert(User user);
}
