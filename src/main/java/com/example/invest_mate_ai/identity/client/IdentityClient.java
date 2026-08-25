package com.example.invest_mate_ai.identity.client;

import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;

public interface IdentityClient {

    IdentityInfo verify(IdentityVerifyRequest request);
}
