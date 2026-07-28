package com.example.invest_mate_ai.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    ACTIVE  ("ACTIVE"  ,"정상"),
    INACTIVE("INACTIVE","휴면"),
    BLOCK   ("BLOCK"   ,"정지"),
    WITHDRAW("WITHDRAW","탈퇴");

    private final String code;
    private final String description;
}