package com.projectboard.config;

import com.projectboard.dto.security.BoardPrincipal;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing  //jpa auditing 기능 활성화
@Configuration  //configuration bean 등록 각종 설정 세팅
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware(){

        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                //SecurityContext 에서 Authentication 을 가져오는 것
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                //UserDetails 의 구현체를 반환
                //getPrincipal 은 인증 정보 반환임 인증 정보라는 것은 여러개 존재 가능
                //그래서 반환형이 object -> 다양한 인증 정보 구현체를 다루기 위함!
                //현재는 UserDetails 라는 구현체를 사용
                .map(Authentication::getPrincipal)
                //그래서 형 변환
//                .map(x -> (BoardPrincipal) x)
                .map(BoardPrincipal.class::cast)
                .map(BoardPrincipal::getUsername);
    }
}
