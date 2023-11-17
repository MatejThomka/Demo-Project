package com.mth.demo.exception.dataexportexceptions;

import com.mth.demo.exception.ExceptionMessage;
import com.mth.demo.exception.PfpException;
import org.springframework.http.HttpStatus;

public class IOException extends PfpException {
  public IOException(String message) {
    super(message, HttpStatus.EXPECTATION_FAILED);
  }

  public IOException() {
    this(ExceptionMessage.CAN_NOT_WRITE_FILE.getMessage());
  }
}
