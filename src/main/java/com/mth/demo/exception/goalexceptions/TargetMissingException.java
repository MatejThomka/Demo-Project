package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class TargetMissingException extends PfpException {
  public TargetMissingException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public TargetMissingException() {
    this(ExceptionMessage.TARGET_MISSING.getMessage());
  }
}
