package com.iam.interviewmate.config;

import com.iam.interviewmate.member.filter.TokenAuthenticationFilter;
import com.iam.interviewmate.member.filter.TokenExceptionFilter;
import com.iam.interviewmate.member.handler.CustomAccessDeniedHandler;
import com.iam.interviewmate.member.handler.CustomAuthenticationEntryPoint;
import com.iam.interviewmate.member.handler.OAuth2FailureHandler;
import com.iam.interviewmate.member.handler.OAuth2SuccessHandler;
import com.iam.interviewmate.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/errer", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // rest api 설정
                .csrf(CsrfConfigurer::disable) // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .cors(CorsConfigurer::disable) // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요 cors(corsCustomizer -> corsCustomizer.configurationSource(customCorsConfigurationSource)
                .httpBasic(HttpBasicConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(FormLoginConfigurer::disable) // OAuth 사용으로 인해 기본 로그인 비활성화
                .logout(LogoutConfigurer::disable) // 기본 logout 비활성화
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

                // request 인증, 인가 설정
                .authorizeHttpRequests(request  ->
                                                 request .requestMatchers(new AntPathRequestMatcher("/"),
                                                                          new AntPathRequestMatcher("/auth/success")).permitAll()
                                                         .anyRequest().authenticated())

                // OAuth 로그인 설정
                .oauth2Login(oauth -> // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                                    oauth.userInfoEndpoint(user -> user.userService(customOAuth2UserService))); // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
//                                         .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 핸들러
//                                         .failureHandler(oAuth2FailureHandler)) // 로그인 실패 시 핸들러

                // jwt 관련 설정
//                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass()) // 토큰 예외 핸들링

                // 인증 예외 핸들링
//                .exceptionHandling(exceptions ->
//                                                exceptions.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                                                          .accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
