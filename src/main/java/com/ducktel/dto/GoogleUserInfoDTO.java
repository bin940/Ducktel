package com.ducktel.dto;

import java.util.Map;

public class GoogleUserInfoDTO implements SocialUserInfoDTO {
    private final Map<String, Object> attributes;

    public GoogleUserInfoDTO(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    @Override
    public String getProvider() {
        return "google";
    }
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
