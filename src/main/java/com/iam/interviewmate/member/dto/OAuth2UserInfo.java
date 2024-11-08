package com.iam.interviewmate.member.dto;


import com.iam.interviewmate.member.entity.Member;
import com.iam.interviewmate.exception.AuthException;
import com.iam.interviewmate.member.enums.Provider;
import com.iam.interviewmate.member.enums.Role;
import lombok.Builder;

import java.util.Map;

import static com.iam.interviewmate.common.ErrorCode.ILLEGAL_REGISTRATION_ID;

@Builder
public record OAuth2UserInfo(String nickname, String email, String profile, String provider, String providerId, String role) {

    public static OAuth2UserInfo of(String registrationId,  Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider("google")
                .providerId((String) attributes.get("sub"))
                .build();
    }

    // TODO nickname에 유니코드도 같이 들어갈 수 있도록 수정
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .nickname((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .provider("kakao")
                .providerId(((Long) attributes.get("id")).toString())
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .nickname((String) response.get("nickname"))
                .email((String) response.get("email"))
                .profile((String) response.get("profile_image"))
                .provider("naver")
                .providerId((String) response.get("id"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .provider(Provider.LOCAL)
                .providerId(providerId)
                .role(Role.MEMBER)
                .build();

    }
}
