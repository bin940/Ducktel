package com.ducktel.dto;

import java.util.Map;

public class NaverUserInfoDTO implements SocialUserInfoDTO {
    private final Map<String, Object> attributes;

    public NaverUserInfoDTO(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProvider() { return "naver";
    }
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
