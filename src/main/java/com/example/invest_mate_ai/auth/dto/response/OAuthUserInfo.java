package com.example.invest_mate_ai.auth.dto.response;

import com.example.invest_mate_ai.auth.type.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {

    private OAuthProvider provider;

    private String providerId;

    private String email;

    private String name;

    private String nickname;
}