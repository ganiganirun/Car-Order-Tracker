package com.example.osid.domain.counsel.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CounselStatus {
    APPLICATION_COMPLETED,   // 신청 완료
    ACCEPTANCE_COMPLETED,    // 접수 완료
    USER_CANCELED,           // 상담 취소
    DEALER_REJECTED,         // 상담 거절
    COUNSEL_COMPLETED;       // 상담 완료

    @JsonCreator
    public static CounselStatus from(String value) {
        return CounselStatus.valueOf(value.toUpperCase());
    }
}
