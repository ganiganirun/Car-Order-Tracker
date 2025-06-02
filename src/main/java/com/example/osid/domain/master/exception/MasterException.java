package com.example.osid.domain.master.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class MasterException extends CustomException {
	public MasterException(BaseCode baseCode) {
		super(baseCode);
	}
}
