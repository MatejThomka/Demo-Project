package com.mth.demo.exception.dataexportexceptions;

import com.mth.demo.exception.ExceptionMessage;
import com.mth.demo.exception.PfpException;
import org.springframework.http.HttpStatus;

public class DateRangeTooBigException extends PfpException {
  public DateRangeTooBigException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }

  public DateRangeTooBigException() {
    this(ExceptionMessage.DATE_RANGE_TOO_BIG.getMessage());
  }
}
