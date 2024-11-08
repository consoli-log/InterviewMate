package com.iam.interviewmate.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("guest"),
    MEMBER("member"),
    ADMIN("admin");

    private final String key;
}
