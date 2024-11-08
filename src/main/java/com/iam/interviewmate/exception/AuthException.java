package com.iam.interviewmate.exception;

import com.iam.interviewmate.common.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
