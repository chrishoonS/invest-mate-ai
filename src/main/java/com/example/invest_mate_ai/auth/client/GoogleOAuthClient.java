package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.GoogleUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.auth.dto.request.GoogleRequest;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthClient implements OAuthClient {

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.GOOGLE;
    }

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

    private final RestClient restClient;

    public GoogleOAuthClient(RestClient restClient) {
        this.restClient = restClient;
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

        if (authCode == null || authCode.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        GoogleRequest googleReq = GoogleRequest.builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(redirectUri)
                .grantType("authorization_code")
                .build();

        try {
            OAuthTokenResponse response = restClient.post()
                    .uri(tokenApiUri)
                    .body(googleReq)
                    .retrieve()
                    .body(OAuthTokenResponse.class);

            if (response == null
                    || response.getAccessToken() == null
                    || response.getAccessToken().isBlank()) {
                throw new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED);
            }

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
        }
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {

        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED);
        }

        try {
            GoogleUserResponse googleUser = restClient.get()
                    .uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(GoogleUserResponse.class);

            if (googleUser == null
                    || googleUser.getSub() == null
                    || googleUser.getSub().isBlank()) {
                throw new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED);
            }

            return OAuthUserInfo.builder()
                    .provider(OAuthProvider.GOOGLE)
                    .providerId(googleUser.getSub())
                    .email(googleUser.getEmail())
                    .name(googleUser.getName())
                    .nickname(googleUser.getGiven_name())
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
        }

    }
}
