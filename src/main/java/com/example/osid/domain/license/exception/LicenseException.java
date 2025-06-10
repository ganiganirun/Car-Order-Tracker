package com.example.osid.domain.license.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class LicenseException extends CustomException {
	public LicenseException(BaseCode baseCode) {
		super(baseCode);
	}
}
