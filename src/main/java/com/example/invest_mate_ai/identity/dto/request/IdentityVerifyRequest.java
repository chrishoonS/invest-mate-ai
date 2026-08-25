package com.example.invest_mate_ai.identity.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentityVerifyRequest {

    private String name;
    private String phone;
    private String birthDate;
}
