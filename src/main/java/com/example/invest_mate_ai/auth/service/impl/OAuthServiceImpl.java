package com.example.invest_mate_ai.auth.service.impl;

import com.example.invest_mate_ai.auth.client.OAuthClient;
import com.example.invest_mate_ai.auth.client.OAuthClientRegistry;
import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.service.OAuthService;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;
import com.example.invest_mate_ai.identity.service.IdentityService;
import com.example.invest_mate_ai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthClientRegistry clientRegistry;
    private final UserService userService;
    private final IdentityService identityService;
    // OAuth 콜백과 별도 본인인증 요청을 연결하는 단기 저장소
    private final Map<String, PendingOAuthLogin> pendingLogins = new ConcurrentHashMap<>();

    @Override
    public String createLoginUrl(OAuthProvider provider) {
        return getClient(provider).createAuthorizationUrl();
    }

    @Override
    public OAuthLoginResponse login(OAuthProvider provider, String code) {
        OAuthClient client = getClient(provider);
        OAuthTokenResponse tokenResponse = client.getAccessToken(code);
        OAuthUserInfo userInfo = client.getUserInfo(tokenResponse.getAccessToken());

        OAuthLoginResponse existingLogin = userService.findLoginResponse(userInfo);
        if (existingLogin != null) {
            return existingLogin;
        }

        // 본인인증을 위한 임시 로그인 아이디(UUID)
        String pendingLoginId = UUID.randomUUID().toString();
        pendingLogins.put(pendingLoginId, new PendingOAuthLogin(userInfo, LocalDateTime.now()));
        return OAuthLoginResponse.builder()
                .registered(false)
                .identityVerificationRequired(true)
                .pendingLoginId(pendingLoginId)
                .message("본인인증이 필요합니다.")
                .build();
    }

    // OAuth 이름과 PASS 인증 이름이 일치할 때만 users 및 oauth_account 연결
    @Override
    public OAuthLoginResponse verifyIdentity(String pendingLoginId, IdentityVerifyRequest identityVerifyRequest) {

        PendingOAuthLogin pendingLogin = getPendingLogin(pendingLoginId);

        IdentityInfo identityInfo = identityService.verify(identityVerifyRequest);
        if (!Objects.equals(pendingLogin.oauthUserInfo().getName(), identityInfo.getName())) {
            throw new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED);
        }

        pendingLogins.remove(pendingLoginId);
        return userService.loginOrRegister(pendingLogin.oauthUserInfo(), identityInfo);
    }

    private OAuthClient getClient(OAuthProvider provider) {
        return clientRegistry.get(provider);
    }

    private PendingOAuthLogin getPendingLogin(String pendingLoginId) {
        PendingOAuthLogin pendingLogin = pendingLogins.get(pendingLoginId);
        if (pendingLogin == null || pendingLogin.createdAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            pendingLogins.remove(pendingLoginId);
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        return pendingLogin;
    }

    private record PendingOAuthLogin(OAuthUserInfo oauthUserInfo, LocalDateTime createdAt) {
    }
}
