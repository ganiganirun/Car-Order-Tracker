package com.example.osid.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode{
    OK(HttpStatus.OK, "S001", "요청을 성공했습니다."),
    CREATED(HttpStatus.CREATED, "S002", "요청이 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
