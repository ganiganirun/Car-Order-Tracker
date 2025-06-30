package com.example.osid.domain.counsel.exception;

import com.example.osid.common.exception.BaseCode;
import com.example.osid.common.exception.CustomException;

public class CounselException extends CustomException {
  public CounselException(BaseCode baseCode) {
    super(baseCode);
  }
}
