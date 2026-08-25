package com.example.invest_mate_ai.auth.service.impl;

import com.example.invest_mate_ai.auth.client.OAuthClient;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthServiceImpl implements OAuthService {

    private final UserService userService;
    private final IdentityService identityService;
    // OAuth 콜백과 별도 본인인증 요청을 연결하는 단기 저장소
    private final Map<String, PendingOAuthLogin> pendingLogins = new ConcurrentHashMap<>();

    // List<OAuthClient>로 생성자 주입/보관
    private final List<OAuthClient> oauthClients;

    public OAuthServiceImpl(List<OAuthClient> oauthClients,
                            UserService userService,
                            IdentityService identityService) {
        // provider 중복 검사
        validateUniqueProviders(oauthClients);
        this.oauthClients = List.copyOf(oauthClients);
        this.userService = userService;
        this.identityService = identityService;
    }

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
        return oauthClients.stream()
                .filter(client -> client.provider() == provider)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER));
    }

    private void validateUniqueProviders(List<OAuthClient> oauthClients) {
        Set<OAuthProvider> providers = new HashSet<>();
        for (OAuthClient oauthClient : oauthClients) {
            if (!providers.add(oauthClient.provider())) {
                throw new IllegalStateException("동일 OAuth Provider의 클라이언트가 중복 등록되었습니다: " + oauthClient.provider());
            }
        }
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
