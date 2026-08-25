package com.example.invest_mate_ai.identity.controller;

import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;
import com.example.invest_mate_ai.identity.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock/pass")
@RequiredArgsConstructor
public class MockPassController {

    private final IdentityService identityService;

    // 추가: 학습/개발 환경에서만 Mock PASS 결과를 확인하기 위한 테스트 API다.
    @PostMapping("/verify")
    public ResponseEntity<IdentityInfo> verify(@RequestBody IdentityVerifyRequest request) {
        return ResponseEntity.ok(identityService.verify(request));
    }
}
