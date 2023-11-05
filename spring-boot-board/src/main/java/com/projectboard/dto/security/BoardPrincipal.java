package com.projectboard.dto.security;

import com.projectboard.dto.UserAccountDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardPrincipal implements UserDetails {
    public String username;
    public String password;
    public Collection<? extends GrantedAuthority> authorities;
    public String email;
    public String nickname;
    public String memo;

    @Builder
    private BoardPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String nickname, String memo) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.getUserId(),
                dto.getUserPassword(),
                dto.getEmail(),
                dto.getNickname(),
                dto.getMemo()
        );
    }

    public UserAccountDto toDto(){
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }

    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return BoardPrincipal
                .builder()
                .username(username)
                .password(password)
                .authorities(roleTypes.stream().map(RoleType::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableSet()))
                .email(email)
                .nickname(nickname)
                .memo(memo)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}

    @Override
    public String getPassword() {return password;}

    @Override
    public String getUsername() {return username;}

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public enum RoleType {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}
