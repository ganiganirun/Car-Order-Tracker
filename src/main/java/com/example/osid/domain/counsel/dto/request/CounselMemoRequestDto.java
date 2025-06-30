package com.example.osid.domain.counsel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounselMemoRequestDto {

    @NotBlank(message = "상담 내용을 작성해주세요.")
    private String memo;
}
