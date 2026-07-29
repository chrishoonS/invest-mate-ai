package com.example.invest_mate_ai.user.vo;

import com.example.invest_mate_ai.user.type.UserStatus;
import lombok.Getter;

import java.util.Date;

@Getter
public class UserVo {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String role;
    private UserStatus userStatus;
    private Date createdAt;
    private Date updatedAt;

}
