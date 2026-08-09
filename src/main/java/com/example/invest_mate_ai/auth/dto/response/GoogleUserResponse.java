package com.example.invest_mate_ai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserResponse {

    private String azp;     // 토큰을 요청한 클라이언트 앱의 ID.(OAuth 클라이언트 ID와 동일)

    private String aud;     // azp와 동일

    private String sub;     // 구글 계정의 고유 사용자 ID

    private String scope;   // 액세스 토큰이 허용하는 권한 범위

    private String email;   // 사용자의 구글 계정 이메일

    @JsonProperty("email_verified")
    private String emailVerified;   // 구글에 의해 검증되었는지 여부

    @JsonProperty("access_type")
    private String accessType;      // 토큰 발급 방식.(online: 단발성, offline은 refresh token 포함)
}