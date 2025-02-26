package com.ducktel.domain.entity;

import com.ducktel.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "name", nullable = false)
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

    public UserDTO getUser(){
        return new UserDTO(
                this.username,
                this.email,
                this.phoneNumber,
                this.name
        );
    }
    public UserDTO updateUser(UserDTO userDTO){
        return userDTO.toBuilder()
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .build();
    }
}