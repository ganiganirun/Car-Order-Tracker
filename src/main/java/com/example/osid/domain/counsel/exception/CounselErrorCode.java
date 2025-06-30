package com.example.osid.domain.counsel.exception;

import com.example.osid.common.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CounselErrorCode implements BaseCode {

    COUNSEL_NOT_FOUND(HttpStatus.NOT_FOUND, "COUNSEL_001", "상담 정보를 찾을 수 없습니다."),
    COUNSEL_NOT_ACCEPT(HttpStatus.NOT_ACCEPTABLE, "COUNSEL_002", "상담 수락 상태가 아니면 메모를 작성할 수 없습니다."),
    COUNSEL_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "COUNSEL_003", "이미 처리된 상담입니다."),
    COUNSEL_NOT_ASSIGNMENT(HttpStatus.BAD_REQUEST, "COUNSEL_004", "해당 상담은 현재 딜러에게 할당되지 않았습니다."),
    COUNSEL_NOT_WRITTEN_MYSELF(HttpStatus.BAD_REQUEST, "COUNSEL_005", "본인이 작성한 상담만 처리할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
