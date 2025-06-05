package com.example.osid.domain.mycar.service;

import org.springframework.data.domain.Page;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;

public interface MyCarService {

	MyCarResponse findMyCar(CustomUserDetails customUserDetails, Long myCarId);

	Page<MyCarListResponse> findAllMyCar(CustomUserDetails customUserDetails, int page, int size);

	void deleteMyCar(CustomUserDetails customUserDetails, Long myCarId);

	MyCarResponse saveMyCar(Long userId, Long orderId);
}
