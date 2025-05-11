package com.ducktel.config.security.service;

import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 로그인 요청 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.debug("OAuth2 사용자 속성 로드 성공: attributes={}", attributes);

        SocialUserInfoDTO socialUserInfo;
        if ("kakao".equals(registrationId)) {
            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.debug("카카오 사용자 정보 요청: accessToken={}", accessToken);
            socialUserInfo = fetchKakaoUserInfo(accessToken);
        } else if ("google".equals(registrationId)) {
            log.debug("구글 사용자 정보 처리 중");
            socialUserInfo = new GoogleUserInfoDTO(attributes);
        } else if ("naver".equals(registrationId)) {
            log.debug("네이버 사용자 정보 처리 중");
            socialUserInfo = new NaverUserInfoDTO(attributes);
        } else {
            log.warn("지원하지 않는 소셜 로그인 플랫폼: {}", registrationId);
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 플랫폼입니다.");
        }

        log.debug("소셜 사용자 정보 변환 성공: socialId={}, provider={}", socialUserInfo.getSocialId(), socialUserInfo.getProvider());

        User user = userRepository.findBySocialId(socialUserInfo.getSocialId())
                .orElseGet(() -> {
                    Optional<User> userByEmail = userRepository.findByEmail(socialUserInfo.getEmail());

                    if (userByEmail.isPresent()) {
                        User existingUser = userByEmail.get();
                        log.debug("기존 사용자 이메일로 조회 성공: email={}", socialUserInfo.getEmail());
                        if (existingUser.getSocialId() == null) {
                            existingUser.setSocialId(socialUserInfo.getSocialId());
                        }
                        if (existingUser.getProvider() == null) {
                            existingUser.setProvider(socialUserInfo.getProvider());
                        }

                        return userRepository.save(existingUser);
                    }

                    log.info("신규 소셜 사용자 저장: socialId={}, email={}", socialUserInfo.getSocialId(), socialUserInfo.getEmail());
                    return saveSocialUser(
                            socialUserInfo.getSocialId(),
                            socialUserInfo.getName(),
                            socialUserInfo.getProvider(),
                            socialUserInfo.getEmail()
                    );
                });

        log.info("OAuth2 로그인 사용자 정보: name={}, socialId={}, email={}, role={}",
                user.getName(), user.getSocialId(), user.getEmail(), user.getRole());

        return new PrincipalDetailDTO(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole())), attributes);
    }

    public User saveSocialUser(String socialId, String name, String provider, String email) {
        log.info("신규 소셜 사용자 저장: socialId={}, name={}, provider={}, email={}", socialId, name, provider, email);
        String userIdUUID = UUID.randomUUID().toString();
        User user = new User();
        user.setUserId(userIdUUID);
        user.setSocialId(socialId);
        user.setName(name);
        user.setProvider(provider);
        user.setEmail(email);
        user.setUsername(email);
        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }

    public KakaoUserInfoDTO fetchKakaoUserInfo(String accessToken) {
        log.info("카카오 사용자 정보를 가져오는 중...");

        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(headers);


        ResponseEntity<Map<String, Object>> response;
        try {
            response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("카카오 API 요청 실패: {}", e.getMessage());
            throw new OAuth2AuthenticationException("카카오 API 요청 실패: " + e.getMessage());
        }

        Map<String, Object> userInfo = response.getBody();
        log.info("카카오 API 응답: {}", userInfo);
        if (userInfo == null) {
            log.error("카카오 사용자 정보가 비어 있습니다.");
            throw new OAuth2AuthenticationException("카카오 사용자 정보를 가져오지 못했습니다.");
        }

        log.info("카카오 사용자 정보 로드 성공: {}", userInfo);
        return new KakaoUserInfoDTO(userInfo);
    }
}
