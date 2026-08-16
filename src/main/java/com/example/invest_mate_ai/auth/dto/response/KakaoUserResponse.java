package com.example.invest_mate_ai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserResponse {

    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {

        private String email;

        private KakaoProfile profile;
    }

    @Getter
    @NoArgsConstructor
    public static class KakaoProfile {

        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }

}
