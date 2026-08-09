package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.KakaoUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoOAuthClient implements OAuthClient{

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.kakao.auth-uri}")
    private String authUri;

    @Value("${oauth.kakao.token-uri}")
    private String tokenUri;

    @Value("${oauth.kakao.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    public KakaoOAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createAuthorizationUrl() {
        return authUri + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
    }

    @Override
    public OAuthTokenResponse getAccessToken(String authCode) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);

        if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
            params.add("client_secret", kakaoClientSecret);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<OAuthTokenResponse> response =
                restTemplate.exchange(
                        tokenUri,
                        HttpMethod.POST,
                        request,
                        OAuthTokenResponse.class
                );

        return response.getBody();

    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity =
                new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response =
                restTemplate.exchange(
                        userInfoUri,
                        HttpMethod.GET,
                        requestEntity,
                        KakaoUserResponse.class
                );

        KakaoUserResponse kakaoUser =
                response.getBody();

        if (kakaoUser == null) {
            throw new IllegalStateException(
                    "카카오 사용자 정보를 가져오지 못했습니다."
            );
        }

        return OAuthUserInfo.builder()
                .provider(OAuthProvider.KAKAO)
                .providerId(String.valueOf(kakaoUser.getId()))
                .email(
                        kakaoUser.getKakaoAccount() != null
                                ? kakaoUser.getKakaoAccount().getEmail()
                                : null
                )
                .name(
                        kakaoUser.getKakaoAccount() != null
                                && kakaoUser.getKakaoAccount().getProfile() != null
                                ? kakaoUser.getKakaoAccount()
                                .getProfile()
                                .getNickname()
                                : null
                )
                .nickname(
                        kakaoUser.getKakaoAccount() != null
                                && kakaoUser.getKakaoAccount().getProfile() != null
                                ? kakaoUser.getKakaoAccount()
                                .getProfile()
                                .getNickname()
                                : null
                )
                .build();
    }
}
