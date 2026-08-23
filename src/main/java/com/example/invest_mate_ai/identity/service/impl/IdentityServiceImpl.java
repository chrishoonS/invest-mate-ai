package com.example.invest_mate_ai.identity.service.impl;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.identity.client.IdentityClient;
import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;
import com.example.invest_mate_ai.identity.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityServiceImpl implements IdentityService {

    private final IdentityClient identityClient;

    // 본인인증을 통해 검증
    @Override
    public IdentityInfo verify(IdentityVerifyRequest request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getPhone()) || isBlank(request.getBirthDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        return identityClient.verify(request);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
