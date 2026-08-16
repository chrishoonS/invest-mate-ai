package com.example.invest_mate_ai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserResponse {
    private String resultcode;

    private String message;

    private UserInfo response;

    @Getter
    @NoArgsConstructor
    public static class UserInfo {

        private String id;

        private String email;

        private String name;

        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        private String age;

        private String gender;

        private String birthday;

        private String birthyear;

        private String mobile;
    }
}
