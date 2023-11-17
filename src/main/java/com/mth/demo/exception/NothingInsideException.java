package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class NothingInsideException extends PfpException {
  public NothingInsideException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public NothingInsideException() {
    this(ExceptionMessage.NOTHING.getMessage());
  }
}
