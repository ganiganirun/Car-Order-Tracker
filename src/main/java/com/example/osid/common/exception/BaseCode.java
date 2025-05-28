package com.example.osid.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();

}
