package com.example.osid.domain.user.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class UserException extends CustomException {
	public UserException(BaseCode baseCode) {
		super(baseCode);
	}
}
