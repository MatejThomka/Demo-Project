package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class DepositMissingException extends PfpException {
  public DepositMissingException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public DepositMissingException() {
    this(ExceptionMessage.DEPOSIT_MISSING.getMessage());
  }
}
