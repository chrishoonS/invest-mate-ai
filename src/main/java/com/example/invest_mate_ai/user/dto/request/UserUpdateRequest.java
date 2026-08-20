package com.example.invest_mate_ai.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {

    private Long id;

    private String name;

    private String nickname;
}