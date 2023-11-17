package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class PasswordIsMissingException extends PfpException {
  public PasswordIsMissingException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public PasswordIsMissingException() {
    this(ExceptionMessage.PASSWORD_MISSING.getMessage());
  }
}
