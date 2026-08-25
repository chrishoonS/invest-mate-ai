package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.dto.response.NaverUserResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthTokenResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverOAuthClient implements OAuthClient {

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.NAVER;
    }

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

    private final RestClient restClient;

    public NaverOAuthClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public String createAuthorizationUrl() {

        return UriComponentsBuilder.fromUriString(authUri)
                .queryParam("response_type", "code")
                .queryParam("client_id", naverClientId)
                .queryParam("redirect_uri", redirectUri)
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
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", authCode);

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
            NaverUserResponse naverUser =
                    restClient.get()
                            .uri(userInfoUri)
                            .headers(headers -> headers.setBearerAuth(accessToken))
                            .retrieve()
                            .body(NaverUserResponse.class);

            if (naverUser == null || naverUser.getResponse() == null) {
                throw new BusinessException(
                        ErrorCode.OAUTH_PROVIDER_FAILURE
                );
            }

            NaverUserResponse.UserInfo user = naverUser.getResponse();

            if (user.getId() == null || user.getId().isBlank()) {
                throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);
            }

            return OAuthUserInfo.builder()
                    .provider(OAuthProvider.NAVER)
                    .providerId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .build();

        } catch (BusinessException e) {
            throw e;

        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.OAUTH_PROVIDER_FAILURE);

        }
    }
}
