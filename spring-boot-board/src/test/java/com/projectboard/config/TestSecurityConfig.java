package com.projectboard.config;

import com.projectboard.domain.UserAccount;
import com.projectboard.repository.UserAccountRepository;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

//controller test 에서 사용되는 security 인증 기능과 관련된 설정을 한 곳으로 집중
@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private UserAccountRepository userAccountRepository;

    //spring 이 지원하는 test
    //Spring Security 는 어쩔 수 없이 Spring 을 사용해야 하는 프레임워크이므로
    //Spring 을 사용하는 이 test 가 적절함
    //Jpa 가 이 메서드를 사용할 수 있도록 Listener 를 통해 전달?
    //각 test method 가 수행되기 직전에 이 메서드 호출 -> 인증 정보 전달
    //이제 이 TestSecurityConfig 가 필요한 Test 에 넣어줘야 한다.
    //EventListener
    @BeforeTestMethod
    public void securitySetup() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "aghTest",
                "pw",
                "agh@gmail.com",
                "agh",
                "memo"
        )));
    }
}
