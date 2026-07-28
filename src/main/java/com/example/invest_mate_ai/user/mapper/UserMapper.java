package com.example.invest_mate_ai.user.mapper;

import com.example.invest_mate_ai.user.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserVo findById(Long id);
}
