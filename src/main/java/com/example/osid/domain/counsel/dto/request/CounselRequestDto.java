package com.example.osid.domain.counsel.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounselRequestDto {

    @Size(max = 20, message = "제목을 20글자 이내로 작성해주세요.")
    private final String title;

    @Size(max = 100, message = "상담 내용은 100자 이내로 작성해주세요.")
    private final String userContent;

    private Long dealerId;
}
