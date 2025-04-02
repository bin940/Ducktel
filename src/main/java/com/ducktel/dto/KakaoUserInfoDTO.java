package com.ducktel.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoUserInfoDTO implements SocialUserInfoDTO {
    private final String socialId;
    private final String name;
    private final String provider;
    private final String email;

    public KakaoUserInfoDTO(Map<String, Object> attributes) {
        this.socialId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {
            log.error("kakao_account가 null입니다!");
            this.name = "Unknown";
            this.email = "Unknown";
        } else {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            if (profile == null) {
                log.error("profile 정보가 없습니다!");
                this.name = "Unknown";
            } else {
                this.name = (String) profile.getOrDefault("nickname", "Unknown");
            }

            this.email = (String) kakaoAccount.getOrDefault("email", "Unknown");
        }

        this.provider = "kakao";
    }

    @Override
    public String getSocialId() {
        return socialId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
