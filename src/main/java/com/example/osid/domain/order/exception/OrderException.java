package com.example.osid.domain.order.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class OrderException extends CustomException {
	public OrderException(BaseCode baseCode) {
		super(baseCode);
	}
}
