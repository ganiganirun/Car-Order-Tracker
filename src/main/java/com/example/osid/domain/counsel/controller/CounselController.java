package com.example.osid.domain.counsel.controller;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.request.CommonPageRequest;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.counsel.dto.request.CounselMemoRequestDto;
import com.example.osid.domain.counsel.dto.request.CounselRequestDto;
import com.example.osid.domain.counsel.dto.response.CounselPagedResponseDto;
import com.example.osid.domain.counsel.dto.response.CounselResponseDto;
import com.example.osid.domain.counsel.service.CounselService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CounselController {

    private final CounselService counselService;

    // 상담 생성
    @PostMapping("/api/users/counsels")
    public CommonResponse<CounselResponseDto> applyCounsel(@RequestBody CounselRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto counsel = counselService.applyCounsel(customUserDetails, requestDto);

        return CommonResponse.created(counsel);
    }

    // 상담 조회(유저)
    @GetMapping("/api/users/counsels")
    public CommonResponse<CounselPagedResponseDto> getUserCounsels(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute CommonPageRequest commonPageRequest) {

        Pageable pageable = commonPageRequest.toPageable();

        Page<CounselResponseDto> counsel = counselService.getUserCounsels(customUserDetails.getId(), pageable);

        return CommonResponse.ok(CounselPagedResponseDto.from(counsel));
    }

    // 상담 조회(딜러)
    @GetMapping("api/dealers/counsels")
    public CommonResponse<CounselPagedResponseDto> getDealerCounsels(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute CommonPageRequest commonPageRequest) {

        Pageable pageable = commonPageRequest.toPageable();

        Page<CounselResponseDto> counsel = counselService.getDealerCounsels(customUserDetails.getId(), pageable);

        return CommonResponse.ok(CounselPagedResponseDto.from(counsel));
    }

    // 상담 취소(유저)
    @PatchMapping("/api/users/counsels/{counselId}/cancel")
    public CommonResponse<CounselResponseDto> cancelCounsel(@PathVariable Long counselId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto counsel = counselService.cancelCounsel(counselId, customUserDetails.getId());

        return CommonResponse.ok(counsel);
    }

    // 상담 수락(딜러)
    @PatchMapping("/api/dealers/counsels/{counselId}/accept")
    public CommonResponse<CounselResponseDto> acceptCounsel(@PathVariable Long counselId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto result = counselService.acceptCounsel(counselId, customUserDetails.getId());

        return CommonResponse.ok(result);
    }

    // 상담 거부(딜러)
    @PatchMapping("/api/dealers/counsels/{counselId}/reject")
    public CommonResponse<CounselResponseDto> rejectCounsel(@PathVariable Long counselId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto result = counselService.rejectCounsel(counselId, customUserDetails.getId());

        return CommonResponse.ok(result);
    }

    // 상담 메모(딜러)
    @PatchMapping("/api/dealers/counsels/{counselId}/memo")
    public CommonResponse<CounselResponseDto> writeMemoAndCompleteCounsel(@PathVariable Long counselId, @RequestBody CounselMemoRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto result = counselService.writeMemoAndCompleteCounsel(counselId, customUserDetails.getId(), requestDto.getMemo());

        return CommonResponse.ok(result);
    }

    // 상담 메모 수정(딜러)
    @PutMapping("/api/dealers/counsels/{counselId}/memo")
    public CommonResponse<CounselResponseDto> updateMemo(@PathVariable Long counselId, @RequestBody CounselMemoRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        CounselResponseDto result = counselService.updateMemo(counselId, customUserDetails.getId(), requestDto.getMemo());

        return CommonResponse.ok(result);
    }
}
