package com.example.invest_mate_ai.auth.mapper;

import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.auth.vo.OAuthAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OAuthAccountMapper {

    OAuthAccount findByProviderInfo( @Param("provider") OAuthProvider provider,
                                 @Param("providerId") String providerId );

    int insertOAuthAccount(OAuthAccount oauthAccount);
}