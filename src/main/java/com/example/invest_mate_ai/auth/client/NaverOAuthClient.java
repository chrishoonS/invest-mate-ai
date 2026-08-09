package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.NaverUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverOAuthClient implements OAuthClient {

    @Value("${oauth.naver.client-id}")
    private String naverClientId;

    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;

    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.naver.auth-uri}")
    private String authUri;

    @Value("${oauth.naver.token-uri}")
    private String tokenUri;

    @Value("${oauth.naver.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;
    private final RestClient restClient;

    public NaverOAuthClient(RestTemplate restTemplate, RestClient restClient) {
        this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    @Override
    public String createAuthorizationUrl() {

        return UriComponentsBuilder
                .fromUriString(authUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", naverClientId)
                .queryParam("redirect_uri", redirectUri)
                .build()
                .encode()
                .toUriString();
    }

    @Override
    public OAuthTokenResponse getAccessToken(String authCode) {

        MultiValueMap<String, String> params =
                new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", authCode);

        return restClient
                .post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(OAuthTokenResponse.class);
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity =
                new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange(
                        userInfoUri,
                        HttpMethod.GET,
                        requestEntity,
                        NaverUserResponse.class
                );

        NaverUserResponse naverUser = response.getBody();

        if (naverUser == null || naverUser.getResponse() == null) {
            throw new IllegalStateException("네이버 사용자 정보를 가져오지 못했습니다.");
        }

        NaverUserResponse.UserInfo user = naverUser.getResponse();

        return OAuthUserInfo.builder()
                .provider(OAuthProvider.NAVER)
                .providerId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
    }
}