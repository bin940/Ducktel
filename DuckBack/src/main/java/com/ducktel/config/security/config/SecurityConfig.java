package com.ducktel.config.security.config;

import com.ducktel.config.security.hadler.FormLoginSuccessHandler;
import com.ducktel.config.security.hadler.OAuth2LoginSuccessHandler;
import com.ducktel.config.security.jwt.JwtVerifyFilter;
import com.ducktel.config.security.service.CustomOauth2UserService;
import com.ducktel.config.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomOauth2UserService customOAuth2UserService;
    private final FormLoginSuccessHandler formLoginSuccessHandler;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    //password BCrypt으로 변환
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        //CORS 설정 객체 생성
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("*")); //허용 도메인
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")); // 허용 HTTP 메서드
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));// 허용 헤더
        corsConfiguration.setAllowCredentials(true);// 쿠키 인증 허용 여부

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);// 모든 경로에 대해 CORS 설정을 적용

        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); //사용자 정보 조회
        authProvider.setPasswordEncoder(passwordEncoder()); // 비밀번호 조회
        return authProvider;
    }
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain");

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // CSRF 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        //JWT 사용으로 세션 STATELESS 설정
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //기본 로그인 제거
        http.formLogin(AbstractHttpConfigurer::disable);

        // 인증 Provider 적용
        http.authenticationProvider(authenticationProvider());

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("Unauthorized: 로그인 필요");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("Forbidden: 접근 권한 없음");
                })
        );

        // 경로 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/favicon.ico","/api/users/register","/api/auth/login","/login/**","/api/home","/api/home/**").permitAll() // 로그인, 회원가입 등 인증 불필요
                .requestMatchers("/api/places/**").permitAll()
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );
        // 일반 로그인 설정
        http.formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler(formLoginSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("일반 로그인 실패: {}", exception.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"error\": \"로그인 실패\"}");
                })
        );
        // OAuth2 설정
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oauth2LoginSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("OAuth2 로그인 실패: {}", exception.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"OAuth2 Login Failed\"}");
                })
        );
        http.addFilterBefore(new JwtVerifyFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new JwtVerifyFilter(), OAuth2LoginAuthenticationFilter.class);


        return http.build();
    }
}
