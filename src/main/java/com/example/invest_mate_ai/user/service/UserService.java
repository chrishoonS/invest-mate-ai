package com.example.invest_mate_ai.user.service;

import com.example.invest_mate_ai.auth.dto.response.OAuthLoginResponse;
import com.example.invest_mate_ai.auth.dto.response.OAuthUserInfo;
import com.example.invest_mate_ai.user.dto.request.UserUpdateRequest;
import com.example.invest_mate_ai.user.dto.response.UserResponse;

public interface UserService {

    OAuthLoginResponse loginOrRegister(OAuthUserInfo oauthUserInfo);

    UserResponse getUsers(Long userId);

    UserResponse updateUsers(UserUpdateRequest request);

    void deleteUsers(Long userId);
}
