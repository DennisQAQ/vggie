package com.dw.vggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dw.vggie.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
