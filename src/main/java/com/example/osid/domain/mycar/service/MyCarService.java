package com.example.osid.domain.mycar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;

public interface MyCarService {

	MyCarResponse findMyCar(CustomUserDetails customUserDetails, Long myCarId);

	Page<MyCarListResponse> findAllMyCar(CustomUserDetails customUserDetails, Pageable pageable);

	void deleteMyCar(CustomUserDetails customUserDetails, Long myCarId);

	MyCarResponse saveMyCar(Long orderId);
}
