package com.mth.demo.exception.dataexportexceptions;

import com.mth.demo.exception.ExceptionMessage;
import com.mth.demo.exception.PfpException;
import org.springframework.http.HttpStatus;

public class DateRangeMissingException extends PfpException {
  public DateRangeMissingException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public DateRangeMissingException() {
    this(ExceptionMessage.DATE_RANGE_MISSING.getMessage());
  }
}
