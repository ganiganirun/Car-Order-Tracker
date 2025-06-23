package com.example.osid.domain.history.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class HistoryException extends CustomException {
	public HistoryException(BaseCode baseCode) {
		super(baseCode);
	}
}
