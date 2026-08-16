package com.example.invest_mate_ai.user.dto.response;

import com.example.invest_mate_ai.user.type.UserRole;
import com.example.invest_mate_ai.user.type.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String nickname;

    private String email;

    private UserRole userRole;

    private UserStatus userStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
