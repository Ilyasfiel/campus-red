package com.campusred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campusred.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT DISTINCT campus FROM user WHERE campus IS NOT NULL AND campus != '' ORDER BY campus")
    List<String> selectDistinctCampuses();
}
