package com.example.invest_mate_ai.auth.controller;

import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.service.OAuthService;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oauthService;

    @GetMapping("/oauth2/{provider}")
    public ResponseEntity<String> login(@PathVariable String provider) {

        OAuthProvider oauthProvider = OAuthProvider.converToProvider(provider);

        String loginUrl = oauthService.createLoginUrl(oauthProvider);

        return ResponseEntity.ok(loginUrl);
    }

    @GetMapping("/oauth2/{provider}/callback")
    public ResponseEntity<?> callback(@PathVariable String provider,
                                      @RequestParam String code) {

        OAuthProvider oauthProvider = OAuthProvider.converToProvider(provider);
        OAuthLoginResponse response = oauthService.login(oauthProvider, code);

        return ResponseEntity.ok(response);
    }

    // OAuth 콜백 이후 Mock PASS 결과로 새 OAuth 계정 연결
    @PostMapping("/oauth2/identity/verify")
    public ResponseEntity<OAuthLoginResponse> verifyIdentity(@RequestParam String pendingLoginId,
                                                              @RequestBody IdentityVerifyRequest request) {
        return ResponseEntity.ok(oauthService.verifyIdentity(pendingLoginId, request));
    }
}
