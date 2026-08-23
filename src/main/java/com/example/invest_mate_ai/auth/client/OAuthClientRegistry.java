package com.example.invest_mate_ai.auth.client;

import com.example.invest_mate_ai.auth.type.OAuthProvider;
import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OAuthClientRegistry {

    private final Map<OAuthProvider, OAuthClient> clients = new EnumMap<>(OAuthProvider.class);

    // 구현체를 늘려도 OAuthService를 수정하지 않도록 Spring이 주입한 목록으로 등록
    public OAuthClientRegistry(List<OAuthClient> oauthClients) {
        for (OAuthClient client : oauthClients) {
            OAuthClient previous = clients.putIfAbsent(client.provider(), client);
            if (previous != null) {
                throw new IllegalStateException("동일 OAuth Provider의 클라이언트가 중복 등록되었습니다: " + client.provider());
            }
        }
    }

    public OAuthClient get(OAuthProvider provider) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return client;
    }
}
