package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class PfpException extends Exception {
  HttpStatus httpStatus;

  public PfpException(String message) {
    super(message);
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public PfpException(String message, HttpStatus status) {
    super(message);
    this.httpStatus = status;
  }
}
