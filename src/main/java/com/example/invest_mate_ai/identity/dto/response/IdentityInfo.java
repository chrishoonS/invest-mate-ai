package com.example.invest_mate_ai.identity.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IdentityInfo {

    private final boolean verified;
    private final String identityKey;
    private final String name;
    private final String phone;
    private final String birthDate;
}
