package com.example.invest_mate_ai.auth.vo;

import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.user.type.UserRole;
import com.example.invest_mate_ai.user.type.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAccount {

    private Long id;
    private Long userId;
    private OAuthProvider provider;
    private String providerId;
    private String providerEmail;
    private String providerName;
    private String providerNickname;
    private LocalDateTime createdAt;
}
