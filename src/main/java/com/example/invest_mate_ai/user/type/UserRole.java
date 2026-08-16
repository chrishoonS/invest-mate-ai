package com.example.invest_mate_ai.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER  ("USER" ,"회원"),
    ADMIN ("ADMIN","관리자");

    private final String code;
    private final String description;
}