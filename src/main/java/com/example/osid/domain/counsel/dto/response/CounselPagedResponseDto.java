package com.example.osid.domain.counsel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CounselPagedResponseDto {

    private List<CounselResponseDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static CounselPagedResponseDto from(org.springframework.data.domain.Page<CounselResponseDto> page) {
        return new CounselPagedResponseDto(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
