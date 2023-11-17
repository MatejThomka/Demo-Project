package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class WithdrawalMissingException extends PfpException {
  public WithdrawalMissingException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public WithdrawalMissingException() {
    this(ExceptionMessage.WITHDRAWAL_MISSING.getMessage());
  }
}