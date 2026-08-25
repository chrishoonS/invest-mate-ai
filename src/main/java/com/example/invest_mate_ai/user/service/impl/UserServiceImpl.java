package com.example.invest_mate_ai.user.service.impl;

import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.auth.mapper.OAuthAccountMapper;
import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.auth.vo.OAuthAccount;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;
import com.example.invest_mate_ai.user.dto.request.UserUpdateRequest;
import com.example.invest_mate_ai.user.dto.response.UserResponse;
import com.example.invest_mate_ai.user.mapper.UserMapper;
import com.example.invest_mate_ai.user.service.UserService;
import com.example.invest_mate_ai.user.type.UserRole;
import com.example.invest_mate_ai.user.type.UserStatus;
import com.example.invest_mate_ai.user.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final OAuthAccountMapper oauthAccountMapper;

    @Override
    public OAuthLoginResponse findLoginResponse(OAuthUserInfo oauthUserInfo) {
        OAuthProvider provider = oauthUserInfo.getProvider();
        String providerId = oauthUserInfo.getProviderId();
        OAuthAccount account = oauthAccountMapper.findByProviderInfo(provider, providerId);

        if (account == null) {
            return null;
        }
        UserVo user = userMapper.findById(account.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return createLoginResponse(true, "이미 인증된 회원입니다.", user.getId());
    }

    // 변경: OAuth 계정이 새로워도 identityKey가 같으면 기존 users에 계정을 연결한다.
    @Override
    public OAuthLoginResponse loginOrRegister(OAuthUserInfo oauthUserInfo, IdentityInfo identityInfo) {
        OAuthLoginResponse existingLogin = findLoginResponse(oauthUserInfo);
        if (existingLogin != null) {
            return existingLogin;
        }

        UserVo user = userMapper.findByIdentityKey(identityInfo.getIdentityKey());
        boolean existingUser = user != null;
        if (!existingUser) {
            user = UserVo.builder()
                    .name(oauthUserInfo.getName())
                    .nickname(oauthUserInfo.getNickname())
                    .email(oauthUserInfo.getEmail())
                    .identityKey(identityInfo.getIdentityKey())
                    .userRole(UserRole.USER)
                    .userStatus(UserStatus.ACTIVE)
                    .build();
            userMapper.insertUsers(user);
        }

        OAuthAccount oauthAccount = OAuthAccount.builder()
                .userId(user.getId())
                .provider(oauthUserInfo.getProvider())
                .providerId(oauthUserInfo.getProviderId())
                .providerEmail(oauthUserInfo.getEmail())
                .providerName(oauthUserInfo.getName())
                .providerNickname(oauthUserInfo.getNickname())
                .build();
        oauthAccountMapper.insertOAuthAccount(oauthAccount);

        String message = existingUser ? "기존 회원 계정이 연결되었습니다." : "회원가입이 완료되었습니다.";
        return createLoginResponse(existingUser, message, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUsers(Long userId) {
        UserVo user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return toResponse(user);
    }

    @Override
    public UserResponse updateUsers(UserUpdateRequest request) {
        UserVo user = userMapper.findById(request.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.updateUsers(request);
        return toResponse(userMapper.findById(request.getId()));
    }

    @Override
    public void deleteUsers(Long userId) {
        UserVo user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getUserStatus() == UserStatus.WITHDRAW) {
            throw new BusinessException(ErrorCode.USER_ALREADY_WITHDRAWN);
        }
        userMapper.updateUserStatus(userId, UserStatus.WITHDRAW);
    }

    private UserResponse toResponse(UserVo user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private OAuthLoginResponse createLoginResponse(boolean registered, String message, Long userId) {
        return OAuthLoginResponse.builder()
                .registered(registered)
                .message(message)
                .userId(userId)
                .build();
    }
}
