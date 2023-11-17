package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends PfpException {

  public UnauthorizedException(String message) {
    super(message, HttpStatus.UNAUTHORIZED);
  }

  public UnauthorizedException() {
    this(ExceptionMessage.UNAUTHORIZED.getMessage());
  }

}