package com.example.osid.event.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class FailedEventException extends CustomException {
	public FailedEventException(BaseCode baseCode) {
		super(baseCode);
	}
}
