package com.iam.interviewmate.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    LOCAL("local"),
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver"),
    GITHUB("github");

    private final String key;
}
