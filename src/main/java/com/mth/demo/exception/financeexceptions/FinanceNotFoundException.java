package com.mth.demo.exception.financeexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class FinanceNotFoundException extends PfpException {

  public FinanceNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public FinanceNotFoundException() {
    this(ExceptionMessage.FINANCE_NOT_EXIST.getMessage());
  }
}
