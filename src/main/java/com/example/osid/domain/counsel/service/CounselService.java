package com.example.osid.domain.counsel.service;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.counsel.dto.request.CounselRequestDto;
import com.example.osid.domain.counsel.dto.response.CounselResponseDto;
import com.example.osid.domain.counsel.entity.Counsel;
import com.example.osid.domain.counsel.enums.CounselStatus;
import com.example.osid.domain.counsel.exception.CounselErrorCode;
import com.example.osid.domain.counsel.exception.CounselException;
import com.example.osid.domain.counsel.repository.CounselRepository;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CounselService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final CounselRepository counselRepository;

    // 상담 신청(유저)
    public CounselResponseDto applyCounsel(CustomUserDetails customUserDetails, CounselRequestDto requestDto) {
        
        User user = getUserByUserId(customUserDetails.getId());

        // 지정 딜러가 있으면 정보 조회 OR 없으면 NULL 반환
        Dealer dealer = requestDto.getDealerId() != null ? getDealerByDealerId(requestDto.getDealerId()) : null;

        Counsel counsel = new Counsel(user, dealer, requestDto.getTitle(), requestDto.getUserContent(), CounselStatus.APPLICATION_COMPLETED);
        return CounselResponseDto.from(counselRepository.save(counsel));
    }

    // 상담 조회(유저)
    public Page<CounselResponseDto> getUserCounsels(Long userId, Pageable pageable) {

        return counselRepository.findAllByUserId(userId, pageable)
                .map(CounselResponseDto::from);
    }

    // 상담 조회(딜러)
    public Page<CounselResponseDto> getDealerCounsels(Long dealerId, Pageable pageable) {

        return counselRepository.findAllByDealerIdOrDealerIsNull(dealerId, pageable)
                .map(CounselResponseDto::from);
    }

    // 상담 취소(유저)
    @Transactional("dataTransactionManager")
    public CounselResponseDto cancelCounsel(Long counselId, Long userId) {
        
        Counsel counsel = getCounselByCounselId(counselId);
        
        // 본인의 상담이 맞는지 확인
        validateUserAuthority(counsel, userId);
        
        // 상담 상태가 접수완료 상태인지(접수가 되면 취소 불가) 확인
        validateCounselStatus(counsel, CounselStatus.APPLICATION_COMPLETED);

        counsel.setCounselStatus(CounselStatus.USER_CANCELED);
        
        return CounselResponseDto.from(counsel);
    }

    // 상담 수락(딜러)
    @Transactional("dataTransactionManager")
    public CounselResponseDto acceptCounsel(Long counselId, Long dealerId) {
        
        Counsel counsel = getCounselByCounselId(counselId);
        
        // 배정된 딜러가 맞는지
        validateDealerAccess(counsel, dealerId);
        
        // 접수완료(대기중) 상태가 맞는 지
        validateCounselStatus(counsel, CounselStatus.APPLICATION_COMPLETED);

        // 딜러가 배정되지 않았다면 배정
        assignDealerIfNotExists(counsel, dealerId);
        
        counsel.setCounselStatus(CounselStatus.ACCEPTANCE_COMPLETED);
        
        return CounselResponseDto.from(counsel);
    }

    // 상담 거절(딜러)
    @Transactional("dataTransactionManager")
    public CounselResponseDto rejectCounsel(Long counselId, Long dealerId) {

        Counsel counsel = getCounselByCounselId(counselId);

        // 배정된 딜러가 맞는지
        validateDealerAccess(counsel, dealerId);

        // 접수완료(대기중) 상태가 맞는 지
        validateCounselStatus(counsel, CounselStatus.APPLICATION_COMPLETED);

        // 딜러가 배정되지 않았다면 배정
        assignDealerIfNotExists(counsel, dealerId);

        counsel.setCounselStatus(CounselStatus.DEALER_REJECTED);

        return CounselResponseDto.from(counsel);
    }

    // 상담 메모(딜러) => 상담 완료
    @Transactional("dataTransactionManager")
    public CounselResponseDto writeMemoAndCompleteCounsel(Long counselId, Long dealerId, String memo) {

        Counsel counsel = getCounselByCounselId(counselId);

        validateDealerAccess(counsel, dealerId);

        validateMemoWritableStatus(counsel);

        counsel.writeMemo(memo);

        counsel.setCounselStatus(CounselStatus.COUNSEL_COMPLETED);

        return CounselResponseDto.from(counsel);
    }

    // 상담 메모 수정
    @Transactional("dataTransactionManager")
    public CounselResponseDto updateMemo(Long counselId, Long dealerId, String newMemo) {

        Counsel counsel = getCounselByCounselId(counselId);

        validateDealerAccess(counsel, dealerId);

        validateMemoWritableStatus(counsel);

        counsel.updateMemo(newMemo);

        return CounselResponseDto.from(counsel);
    }

    private void validateUserAuthority(Counsel counsel, Long userId) {

        if (!counsel.getUser().getId().equals(userId)) {
            throw new CounselException(CounselErrorCode.COUNSEL_NOT_WRITTEN_MYSELF);
        }
    }

    private void validateDealerAccess(Counsel counsel, Long dealerId) {

        if (counsel.getDealer() != null && !counsel.getDealer().getId().equals(dealerId)) {
            throw new CounselException(CounselErrorCode.COUNSEL_NOT_ASSIGNMENT);
        }
    }

    private void validateCounselStatus(Counsel counsel, CounselStatus expected) {

        if (!counsel.getCounselStatus().equals(expected)) {
            throw new CounselException(CounselErrorCode.COUNSEL_ALREADY_PROCESSED);
        }
    }

    private void validateMemoWritableStatus(Counsel counsel) {

        if (counsel.getCounselStatus() != CounselStatus.ACCEPTANCE_COMPLETED) {
            throw new CounselException(CounselErrorCode.COUNSEL_NOT_ACCEPT);
        }
    }

    private void assignDealerIfNotExists(Counsel counsel, Long dealerId) {

        if (counsel.getDealer() == null) {
            Dealer dealer = getDealerByDealerId(dealerId);
            counsel.setDealer(dealer);
        }
    }

    public Counsel getCounselByCounselId(Long counselId) {

        return counselRepository.findById(counselId)
                .orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));
    }

    public User getUserByUserId(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    public Dealer getDealerByDealerId(Long dealerId) {

        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));
    }
}

