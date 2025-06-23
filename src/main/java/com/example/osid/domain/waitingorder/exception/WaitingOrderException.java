package com.example.osid.domain.waitingorder.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class WaitingOrderException extends CustomException {
	public WaitingOrderException(BaseCode baseCode) {
		super(baseCode);
	}
}
