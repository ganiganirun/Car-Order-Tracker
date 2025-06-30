package com.example.osid.domain.counsel.exception;

public class CounselException extends RuntimeException {
  public CounselException(CounselErrorCode message) {
    super(message);
  }
}
