package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.GoogleUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.auth.dto.request.GoogleRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthClient implements OAuthClient {

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.google.google-auth-uri}")
    private String googleAuthUri;

    @Value("${oauth.google.token-api-uri}")
    private String tokenApiUri;

    @Value("${oauth.google.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    public GoogleOAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createAuthorizationUrl() {

        return UriComponentsBuilder
                .fromUriString(googleAuthUri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile openid")
                .build()
                .encode()
                .toUriString();

    }

    @Override
    public OAuthTokenResponse getAccessToken(String authCode) {

        // Google Token API 호출
        GoogleRequest googleReq = GoogleRequest.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(redirectUri)
                .grantType("authorization_code").build();

        ResponseEntity<OAuthTokenResponse> response =
                restTemplate.postForEntity(tokenApiUri, googleReq, OAuthTokenResponse.class);

        return response.getBody();
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response =
                restTemplate.exchange(
                        userInfoUri,
                        HttpMethod.GET,
                        requestEntity,
                        GoogleUserResponse.class
                );

        GoogleUserResponse googleUser = response.getBody();

        return OAuthUserInfo.builder()
                .provider(OAuthProvider.GOOGLE)
                .providerId(googleUser.getSub())
                .email(googleUser.getEmail())
                .name(googleUser.getName())
                .nickname(googleUser.getGiven_name())
                .build();

    }
}
