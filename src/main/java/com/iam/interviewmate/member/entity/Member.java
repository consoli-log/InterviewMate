package com.iam.interviewmate.member.entity;

import com.iam.interviewmate.common.BaseTime;
import com.iam.interviewmate.member.enums.Provider;
import com.iam.interviewmate.member.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_seq")
    private long id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column()
    private String password; // 일반 로그인 비밀번호

    @Column()
    private String nickname; // 닉네임

    @Column()
    private String profile; // 프로필 사진

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider; // 로그인 구분

    @Column(nullable = false)
    private String providerId; // 소셜로그인 식별코드

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    @Builder
    public Member (String email, String password, String nickname, String profile, Provider provider, String providerId, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile = profile;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

}
