package com.mth.demo.exception.goalexceptions;

import com.mth.demo.exception.*;
import org.springframework.http.HttpStatus;

public class GoalAlreadyExistException extends PfpException {
  public GoalAlreadyExistException(String message) {
    super(message, HttpStatus.CONFLICT);
  }

  public GoalAlreadyExistException() {
    this(ExceptionMessage.GOAL_ALREADY_EXIST.getMessage());
  }
}
