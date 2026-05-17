package com.campusred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campusred.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT DISTINCT partner_id FROM ("
            + "SELECT to_user_id AS partner_id FROM message WHERE from_user_id = #{userId} "
            + "UNION "
            + "SELECT from_user_id AS partner_id FROM message WHERE to_user_id = #{userId}"
            + ") t")
    List<Long> selectDistinctPartnerIds(@Param("userId") Long userId);
}
