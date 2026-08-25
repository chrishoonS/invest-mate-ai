package com.example.invest_mate_ai.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthLoginResponse {

    private boolean registered;

    private String message;

    private Long userId;

    // 새 OAuth 계정의 본인인증 완료 대기 여부
    private boolean identityVerificationRequired;

    // 콜백 이후 별도 본인인증 요청에서 사용할 임시 로그인 식별자
    private String pendingLoginId;
}
