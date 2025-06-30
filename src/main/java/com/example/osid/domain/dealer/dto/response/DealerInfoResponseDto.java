package com.example.osid.domain.dealer.dto.response;

import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.enums.Branch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DealerInfoResponseDto {
	private Long id;
	private String email;
	private String name;
	private String phoneNumber;
	private Branch branch;
	private Role role;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createdAt;
}
