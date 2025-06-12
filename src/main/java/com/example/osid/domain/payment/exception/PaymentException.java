package com.example.osid.domain.payment.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class PaymentException extends CustomException {
	public PaymentException(BaseCode baseCode) {
		super(baseCode);
	}
}
