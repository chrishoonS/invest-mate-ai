package com.example.invest_mate_ai.identity.service;

import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;

public interface IdentityService {

    IdentityInfo verify(IdentityVerifyRequest request);
}
