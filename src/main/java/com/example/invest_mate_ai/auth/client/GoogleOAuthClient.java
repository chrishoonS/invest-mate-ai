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

@Component
public class GoogleOAuthClient implements OAuthClient {

    @Value("${google.oauth.client-id}")
    private String googleClientId;

    @Value("${google.oauth.client-secret}")
    private String googleClientSecret;

    @Value("${google.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${google.oauth.google-auth-uri}")
    private String googleAuthUri;

    @Value("${google.oauth.token-api-uri}")
    private String tokenApiUri;

    @Value("${google.oauth.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    public GoogleOAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createAuthorizationUrl() {

        return googleAuthUri + "?client_id=" + googleClientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=email%20profile%20openid";
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
                .build();

    }
}
