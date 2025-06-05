package com.example.osid.domain.option.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class OptionException extends CustomException {
	public OptionException(BaseCode baseCode) {
		super(baseCode);
	}
}
