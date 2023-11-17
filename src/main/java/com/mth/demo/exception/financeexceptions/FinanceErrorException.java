package com.mth.demo.exception.financeexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class FinanceErrorException extends PfpException {

  public FinanceErrorException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public FinanceErrorException() {
    this(ExceptionMessage.FINANCE_ERROR.getMessage());
  }
}
