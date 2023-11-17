package com.mth.demo.exception;

import java.util.Arrays;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@AllArgsConstructor
public enum ExceptionMessage {
  DEFAULT("Some error occurred!"), UNAUTHORIZED("You are not authorized!"),
  USER_NOT_FOUND("User does not exist!"), USER_ALREADY_EXIST("User is already exist!"),
  GOAL_ALREADY_EXIST("Goal already exist under this name, please use different name!"),
  MISSING_NAME("Name of goal is not presented! Please provide a name."),
  TARGET_MISSING("Target for goal is not presented!"),
  GOAL_NOT_EXIST("Provided financial goal doesn't exist!"),
  DEPOSIT_MISSING("Please provide a deposit!"), WITHDRAWAL_MISSING("Please provide a withdrawal!"),
  BALANCE_LOW("Your current balance of this goal is to low!"),
  FINANCE_ERROR("Some error in your finance occurred!"),
  FINANCE_NOT_EXIST("Finance with provided ID not found!"),
  NOTHING("There is nothing, add something firsts!"), DATE_RANGE_TOO_BIG("Date range is too big!"),
  DATE_RANGE_MISSING("Date range is missing!"), CAN_NOT_WRITE_FILE("Can not write file!"),
  EMAIL_MISSING("Email is not presented!"), PASSWORD_MISSING("Password is not presented!"),
  NOTHING_TO_CHANGE("Nothing was edited because you are not provided any of values!");

  private final String message;

  public String getMessage() {
    try {
      checkParametrization();
      return message;
    } catch (MissingParameterException exception) {
      log.error(exception.getMessage());
      return Arrays.stream(ExceptionMessage.values()).filter(
              exceptionString -> exceptionString.name()
                  .equalsIgnoreCase(name().split("_PARAMETRIZED")[0])).findFirst()
          .orElse(ExceptionMessage.DEFAULT).getMessage();
    }
  }

  public String getMessage(Object... args) {
    return String.format(message, args);
  }

  private void checkParametrization() throws MissingParameterException {
    if (this.name().toLowerCase(Locale.ROOT).contains("parametrized")) {
      throw new MissingParameterException("Missing parameter for exception string " + name());
    }
  }
}
