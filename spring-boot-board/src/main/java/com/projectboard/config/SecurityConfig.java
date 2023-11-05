package com.projectboard.config;

import com.projectboard.dto.UserAccountDto;
import com.projectboard.dto.security.BoardPrincipal;
import com.projectboard.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(HttpMethod.GET, "/", "/articles", "/articles/search-hashtag").permitAll().anyRequest().authenticated()
                )
                .formLogin().and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
                .build();
    }

    //spring security 를 쓰기 때문에 사용자 정보 가져오는 부분을 구현해서 넣어줘야한다.
    //유저 찾기
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        //UserDetailsService 인터페이스의 loadByUsername 을 구현...
        //UserDetailsService 가 구현해야할 메서드가 한개 뿐이어서 아래와 같이 람다식 작성으로 한번에 구현 가능

        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                //loadByUsername 의 throw 가 UsernameNotFoundException 임
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username" + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
