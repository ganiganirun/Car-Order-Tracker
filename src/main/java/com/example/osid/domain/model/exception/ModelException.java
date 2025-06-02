package com.example.osid.domain.model.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class ModelException extends CustomException {
	public ModelException(BaseCode baseCode) {
		super(baseCode);
	}
}
