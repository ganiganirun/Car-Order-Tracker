package com.example.osid.domain.waitingorder.enums;

public enum WaitingStatus {
	WAITING,        // 초기 상태
	PROCESSING,        // 처리 중 (예측 준비 완료)
	COMPLETED        // 예측 결과 저장 완료
}
