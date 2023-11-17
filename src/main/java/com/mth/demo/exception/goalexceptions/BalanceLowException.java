package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class BalanceLowException extends PfpException {
  public BalanceLowException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public BalanceLowException() {
    this(ExceptionMessage.BALANCE_LOW.getMessage());
  }
}
