package com.example.osid.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final BaseCode baseCode;

    public CustomException(BaseCode baseCode) {
        super(baseCode.getMessage());
        this.baseCode = baseCode;
    }

    public CustomException(BaseCode baseCode, String message) {
        super(message);
        this.baseCode = baseCode;
    }
}
