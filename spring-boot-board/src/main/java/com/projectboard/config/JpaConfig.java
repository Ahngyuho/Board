package com.projectboard.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing  //jpa auditing 기능 활성화
@Configuration  //configuration bean 등록 각종 설정 세팅
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware(){
        return () -> Optional.of("agh");    //나중에 반드시 바꿔줘야 됨
                                                //스프링 인증 기능 사용 시 수정
    }
}
