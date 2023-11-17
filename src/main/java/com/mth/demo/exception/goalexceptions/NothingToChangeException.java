package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class NothingToChangeException extends PfpException {
  public NothingToChangeException(String message) {
    super(message, HttpStatus.EXPECTATION_FAILED);
  }

  public NothingToChangeException() {
    this(ExceptionMessage.NOTHING_TO_CHANGE.getMessage());
  }
}
