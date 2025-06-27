package com.example.osid.domain.option.enums;

import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OptionCategory {
	NAVIGATION("내비게이션"),          // 내비게이션
	SUNROOF("선루프"),             // 선루프
	HEATED_SEATS("열선 시트"),        // 열선 시트
	VENTILATED_SEATS("통풍 시트"),    // 통풍 시트
	LEATHER_SEATS("가죽 시트"),       // 가죽 시트
	PARKING_SENSOR("주차 센서"),      // 주차 센서
	REAR_CAMERA("후방 카메라"),         // 후방 카메라
	CRUISE_CONTROL("크루즈 컨트롤"),      // 크루즈 컨트롤
	BLIND_SPOT_MONITOR("사각지대 감지 시스템"),  // 사각지대 감지 시스템
	LANE_KEEP_ASSIST("차선 유지 보조"),    // 차선 유지 보조
	ADAPTIVE_HEADLIGHTS("어댑티브 헤드라이트"), // 어댑티브 헤드라이트
	AUTO_PARKING("자동 주차 보조"),        // 자동 주차 보조
	HEAD_UP_DISPLAY("헤드업 디스플레이"),     // 헤드업 디스플레이
	WIRELESS_CHARGER("무선 충전"),    // 무선 충전
	BOSE_SOUND_SYSTEM("프리미엄 오디오"),   // 프리미엄 오디오 (BOSE 등)
	AWD("사륜 구동 시스템"),                 // 사륜 구동 시스템
	SMART_KEY("스마트 키"),           // 스마트 키
	POWER_TAILGATE("전동식 트렁크"),      // 전동식 트렁크
	DASHCAM("블랙박스"),             // 블랙박스
	REMOTE_START("원격 시동");       // 원격 시동

	private final String displayOptionCategory;

	@JsonCreator
	public static OptionCategory from(String inputCategory) {
		for (OptionCategory category : com.example.osid.domain.option.enums.OptionCategory.values()) {
			if (category.name().equalsIgnoreCase(inputCategory)) {
				return category;
			}
		}
		throw new ModelException(ModelErrorCode.INVALID_MODEL_CATEGORY);
	}
}
