package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class GoalNotExistException extends PfpException {
  public GoalNotExistException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public GoalNotExistException() {
    this(ExceptionMessage.GOAL_NOT_EXIST.getMessage());
  }
}
