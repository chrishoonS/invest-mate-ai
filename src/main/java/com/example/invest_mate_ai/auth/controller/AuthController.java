package com.example.invest_mate_ai.auth.controller;

import com.example.invest_mate_ai.auth.service.OAuthService;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OAuthService oauthService;

    public AuthController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }

    @GetMapping("/oauth2/{provider}")
    public ResponseEntity<String> login(
            @PathVariable String provider) {

        OAuthProvider oauthProvider = OAuthProvider.converToProvider(provider);

        String loginUrl = oauthService.createLoginUrl(oauthProvider);

        return ResponseEntity.ok(loginUrl);
    }

    @GetMapping("/oauth2/{provider}/callback")
    public ResponseEntity<?> callback(@PathVariable String provider,
                                      @RequestParam String code) {

        OAuthProvider oauthProvider = OAuthProvider.converToProvider(provider);

        return ResponseEntity.ok(oauthService.login(oauthProvider, code));
    }
}
