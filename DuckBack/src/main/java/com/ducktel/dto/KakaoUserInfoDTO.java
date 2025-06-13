package com.ducktel.dto;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KakaoUserInfoDTO implements SocialUserInfoDTO {
    private final String socialId;
    private final String name;
    private final String provider;
    private final String email;

    @SuppressWarnings("unchecked")
    public KakaoUserInfoDTO(Map<String, Object> attributes) {
        this.socialId = String.valueOf(attributes.get("id"));
        Object kakaoAccountObj = attributes.get("kakao_account");
        Map<String, Object> kakaoAccount = null;

        if (!(kakaoAccountObj instanceof Map)) {
            log.error("kakao_account가 Map 형식이 아닙니다!");
            this.name = "Unknown";
            this.email = "Unknown";
        } else {
            kakaoAccount = (Map<String, Object>) kakaoAccountObj;
            Object profileObj = kakaoAccount.get("profile");
            Map<String, Object> profile = null;

            if (!(profileObj instanceof Map)) {
                log.error("profile이 Map 형식이 아닙니다!");
                this.name = "Unknown";
            } else {
                profile = (Map<String, Object>) profileObj;
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
