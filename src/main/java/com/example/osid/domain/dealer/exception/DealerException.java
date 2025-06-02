package com.example.osid.domain.dealer.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class DealerException extends CustomException {
	public DealerException(BaseCode baseCode) {
		super(baseCode);
	}
}
