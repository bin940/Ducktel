package com.ducktel.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String password;

    private String email;

    private String role;

    private String phoneNumber;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private LocalDateTime deleted_at;

    private String socialId;

    private String provider;

    private String name;


    //save() 되기 전 실행 현재 시간 저장
    @PrePersist
    public void prePersist() {
        this.created_at = this.created_at == null ? LocalDateTime.now() : this.created_at;
        this.updated_at = this.updated_at == null ? LocalDateTime.now() : this.updated_at;
    }
    //사용자 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> role);
    }
    //계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}