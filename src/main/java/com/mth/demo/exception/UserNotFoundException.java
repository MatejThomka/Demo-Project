package com.mth.demo.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends PfpException {

  public UserNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public UserNotFoundException() {
    this(ExceptionMessage.USER_NOT_FOUND.getMessage());
  }

}