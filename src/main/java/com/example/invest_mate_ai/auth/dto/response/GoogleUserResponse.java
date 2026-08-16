package com.example.invest_mate_ai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserResponse {

    private String sub;
    private String email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
}