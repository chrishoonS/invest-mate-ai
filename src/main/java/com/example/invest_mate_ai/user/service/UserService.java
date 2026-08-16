package com.example.invest_mate_ai.user.service;

import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.mapper.OAuthAccountMapper;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.auth.vo.OAuthAccount;
import com.example.invest_mate_ai.user.dto.request.UserUpdateRequest;
import com.example.invest_mate_ai.user.dto.response.UserResponse;
import com.example.invest_mate_ai.user.mapper.UserMapper;
import com.example.invest_mate_ai.user.type.UserRole;
import com.example.invest_mate_ai.user.type.UserStatus;
import com.example.invest_mate_ai.user.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final OAuthAccountMapper oauthAccountMapper;

    public OAuthLoginResponse loginOrRegister(OAuthUserInfo oauthUserInfo) {

        OAuthProvider provider = oauthUserInfo.getProvider();
        String      providerId = oauthUserInfo.getProviderId();

        OAuthAccount account = oauthAccountMapper.findByProviderInfo(provider, providerId);

        // 해당 provider로 이미 oauth 인증 된 회원
        if (account != null) {

            UserVo user = userMapper.findById(account.getUserId());

            return OAuthLoginResponse.builder()
                    .registered(true)
                    .message("이미 인증된 회원입니다.")
                    .userId(user.getId())
                    .build();
        }

        // 신규 회원
        UserVo user = UserVo.builder()
                .name(oauthUserInfo.getName())
                .nickname(oauthUserInfo.getNickname())
                .email(oauthUserInfo.getEmail())
                .role(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();

        userMapper.insertUsers(user);

        OAuthAccount oauthAccount = OAuthAccount.builder()
                        .userId(user.getId())
                        .provider(oauthUserInfo.getProvider())
                        .providerId(oauthUserInfo.getProviderId())
                        .providerEmail(oauthUserInfo.getEmail())
                        .providerName(oauthUserInfo.getName())
                        .providerNickname(oauthUserInfo.getNickname())
                        .build();

        oauthAccountMapper.insertOAuthAccount(oauthAccount);

        return OAuthLoginResponse.builder()
                .registered(false)
                .message("회원가입이 완료되었습니다.")
                .userId(user.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUsers(Long userId) {

        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userRole(user.getRole())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponse updateUsers(UserUpdateRequest request) {
        Long userId = request.getId();
        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        userMapper.updateUsers(request);

        UserVo updatedUser = userMapper.findById(userId);

        return UserResponse.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .nickname(updatedUser.getNickname())
                .email(updatedUser.getEmail())
                .userRole(updatedUser.getRole())
                .userStatus(updatedUser.getUserStatus())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    public void deleteUsers(Long userId) {

        UserVo user = userMapper.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        if (user.getUserStatus() == UserStatus.WITHDRAW) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        userMapper.updateUserStatus(userId, UserStatus.WITHDRAW);
    }
}