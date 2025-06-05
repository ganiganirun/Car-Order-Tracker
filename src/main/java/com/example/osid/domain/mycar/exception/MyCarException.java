package com.example.osid.domain.mycar.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class MyCarException extends CustomException {
	public MyCarException(BaseCode baseCode) {
		super(baseCode);
	}
}
