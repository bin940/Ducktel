package com.ducktel.dto;

import com.ducktel.domain.entity.User;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@Slf4j
public class PrincipalDetailDTO implements UserDetails, OAuth2User {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Map<String, Object> attributes;

    public PrincipalDetailDTO(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
        this.attributes = null; //OAuth2 인증이 아닐 때 null
    }

    public PrincipalDetailDTO(User user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        log.info("PrincipalDetailDTO 생성 시작 - user={}, authorities={}, attributes={}",
                user != null ? user.getUsername() : "NULL",
                authorities, attributes);
        if (user == null) {
            log.error("PrincipalDetailDTO 생성 시 user가 null입니다.");
            throw new IllegalArgumentException("User 정보가 null입니다.");
        }
        this.user = user;
        this.authorities = authorities;
        this.attributes = attributes;
    }
    public Map<String, Object> getUserInfo() {
        Map<String, Object> info = new HashMap<>();
        if (user == null) {
            info.put("error", "User 정보가 없습니다.");
            return info;
        }
        log.info("PrincipalDetailDTO 생성 - user.name={}", user.getName());
        info.put("name", user.getUsername());
        info.put("email", user.getEmail());
        info.put("role", user.getRole());
        return info;
    }


    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
