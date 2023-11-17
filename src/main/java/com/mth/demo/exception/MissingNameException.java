package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class MissingNameException extends PfpException {
  public MissingNameException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public MissingNameException() {
    this(ExceptionMessage.MISSING_NAME.getMessage());
  }
}
