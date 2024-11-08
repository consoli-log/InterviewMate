package com.iam.interviewmate.member.service;

import com.iam.interviewmate.member.dto.OAuth2UserInfo;
import com.iam.interviewmate.member.dto.OAuth2UserPrincipal;
import com.iam.interviewmate.member.entity.Member;
import com.iam.interviewmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User (oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                                                        .getUserInfoEndpoint().getUserNameAttributeName();
//        String accessToken = oAuth2UserRequest.getAccessToken().getTokenValue();

        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2User.getAttributes());


        Member member = getOrSave(oAuth2UserInfo);

        return new OAuth2UserPrincipal(member, oAuth2User.getAttributes(), userNameAttributeName);
    }

    private Member getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByEmail(oAuth2UserInfo.email())
                                        .orElseGet(oAuth2UserInfo::toEntity);

        return memberRepository.save(member);
    }
}
