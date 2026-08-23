package com.example.invest_mate_ai.identity.client;

import com.example.invest_mate_ai.common.exception.BusinessException;
import com.example.invest_mate_ai.common.exception.ErrorCode;
import com.example.invest_mate_ai.identity.dto.request.IdentityVerifyRequest;
import com.example.invest_mate_ai.identity.dto.response.IdentityInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!prd")
public class MockPassClient implements IdentityClient {

    private static final List<MockIdentity> MOCK_IDENTITIES = List.of(
            // 테스트용
            new MockIdentity("MOCK-001", "송지훈", "01051837290", "19901103"),
            new MockIdentity("MOCK-002", "김철수", "01022223333", "19920520"),
            new MockIdentity("MOCK-003", "이영희", "01033334444", "19880715"),
            new MockIdentity("MOCK-004", "박민수", "01044445555", "19951230"),
            new MockIdentity("MOCK-005", "최지은", "01055556666", "20000112")
    );

    @Override
    public IdentityInfo verify(IdentityVerifyRequest request) {
        MockIdentity identity = MOCK_IDENTITIES.stream()
                .filter(candidate -> candidate.matches(request))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.IDENTITY_VERIFICATION_FAILED));

        return IdentityInfo.builder()
                .verified(true)
                .identityKey(identity.identityKey())
                .name(identity.name())
                .phone(identity.phone())
                .birthDate(identity.birthDate())
                .build();
    }

    private record MockIdentity(String identityKey, String name, String phone, String birthDate) {

        private boolean matches(IdentityVerifyRequest request) {
            return name.equals(request.getName())
                    && phone.equals(request.getPhone())
                    && birthDate.equals(request.getBirthDate());
        }
    }
}
