package com.example.osid.domain.counsel.dto.response;

import com.example.osid.domain.counsel.entity.Counsel;
import com.example.osid.domain.counsel.enums.CounselStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounselResponseDto {

    private final Long id;

    private final String userName;

    private final String userPhoneNumber;

    private final String title;

    private final String userContent;

    private final CounselStatus counselStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String dealerName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String dealerMemo;

    public static CounselResponseDto from(Counsel counsel) {
        return new CounselResponseDto(
                counsel.getId(),
                counsel.getUser().getName(),
                counsel.getUser().getPhoneNumber(),
                counsel.getTitle(),
                counsel.getUserContent(),
                counsel.getCounselStatus(),
                counsel.getDealer() != null ? counsel.getDealer().getName() : null,
                counsel.getDealerMemo()
        );
    }
}
