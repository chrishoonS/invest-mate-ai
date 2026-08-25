package com.example.invest_mate_ai.user.mapper;

import com.example.invest_mate_ai.user.dto.request.UserUpdateRequest;
import com.example.invest_mate_ai.user.type.UserStatus;
import com.example.invest_mate_ai.user.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserVo findById(Long id);

    UserVo findByIdentityKey(String identityKey);

    void updateUsers(UserUpdateRequest request);

    int insertUsers(UserVo user);

    void updateUserStatus(Long userId, UserStatus userStatus);
}
