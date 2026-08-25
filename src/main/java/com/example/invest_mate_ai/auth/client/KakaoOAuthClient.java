package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.KakaoUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class KakaoOAuthClient implements OAuthClient{

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.KAKAO;
    }

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

    private final RestClient restClient;

    public KakaoOAuthClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String createAuthorizationUrl() {
        return UriComponentsBuilder.fromUriString(authUri)
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build()
                .encode()
                .toUriString();
    }

    @Override
    public OAuthTokenResponse getAccessToken(String authCode) {

        if (authCode == null || authCode.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);

        if (kakaoClientSecret != null && !kakaoClientSecret.isBlank()) {
            params.add("client_secret", kakaoClientSecret);
        }

        try{

            OAuthTokenResponse response = restClient
                    .post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(OAuthTokenResponse.class);

            if(response == null
                    ||response.getAccessToken() == null
                    || response.getAccessToken().isBlank()) {
                throw new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED);
            }

            return response;

        }catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
        }

        try {
            KakaoUserResponse kakaoUser =
                    restClient.get()
                            .uri(userInfoUri)
                            .headers(headers -> headers.setBearerAuth(accessToken))
                            .retrieve()
                            .onStatus(status -> status.isError(),
                                    (request, response) -> {
                                log.error("카카오 사용자 정보 조회 실패. status={}", response.getStatusCode());
                                throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
                            })
                            .body(KakaoUserResponse.class);

            if (kakaoUser == null || kakaoUser.getId() == null) {
                log.error("카카오 사용자 정보 응답이 올바르지 않습니다.");

                throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
            }

            KakaoUserResponse.KakaoAccount kakaoAccount = kakaoUser.getKakaoAccount();

            KakaoUserResponse.KakaoProfile profile = kakaoAccount != null
                    ? kakaoAccount.getProfile()
                    : null;

            return OAuthUserInfo.builder()
                    .provider(OAuthProvider.KAKAO)
                    .providerId(String.valueOf(kakaoUser.getId()))
                    .email(kakaoAccount != null ? kakaoAccount.getEmail() : null)
                    .name(profile != null ? profile.getNickname() : null)
                    .nickname(profile != null ? profile.getNickname() : null)
                    .build();

        } catch (BusinessException e) {
            throw e;

        } catch (RestClientException e) {
            log.error("카카오 사용자 정보 API 호출 중 오류가 발생했습니다.", e);
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
        }
    }
}