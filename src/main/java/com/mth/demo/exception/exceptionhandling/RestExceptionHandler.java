package com.mth.demo.exception.exceptionhandling;

import com.mth.demo.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  protected ResponseEntity<Object> handleUnauthorized(UnauthorizedException exception) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  protected ResponseEntity<Object> handleUserAlreadyExist(UserAlreadyExistsException exception) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(UserNotFoundException.class)
  protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(BalanceLowException.class)
  protected ResponseEntity<Object> handleBalanceLow(BalanceLowException exception) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DepositMissingException.class)
  protected ResponseEntity<Object> handleDepositMissing(DepositMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(GoalAlreadyExistException.class)
  protected ResponseEntity<Object> handleGoalAlreadyExist(GoalAlreadyExistException exception) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(GoalNotExistException.class)
  protected ResponseEntity<Object> handleGoalNotExist(GoalNotExistException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(MissingNameException.class)
  protected ResponseEntity<Object> handleMissingName(MissingNameException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(TargetMissingException.class)
  protected ResponseEntity<Object> handleTargetMissing(TargetMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(WithdrawalMissingException.class)
  protected ResponseEntity<Object> handleWithdrawalMissing(WithdrawalMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FinanceNotFoundException.class)
  protected ResponseEntity<Object> handleFinanceNotFound(FinanceNotFoundException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(FinanceErrorException.class)
  protected ResponseEntity<Object> handleFinanceError(FinanceErrorException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(NothingInsideException.class)
  protected ResponseEntity<Object> handleNothingInside(NothingInsideException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(IOException.class)
  protected ResponseEntity<Object> handleCanNotWriteFile(IOException exception) {
    ApiError apiError = new ApiError(HttpStatus.EXPECTATION_FAILED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DateRangeMissingException.class)
  protected ResponseEntity<Object> handleDateRangeMissing(DateRangeMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(DateRangeTooBigException.class)
  protected ResponseEntity<Object> handleDateRangeTooBig(DateRangeTooBigException exception) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(EmailIsMissingException.class)
  protected ResponseEntity<Object> handleEmailIsMissing(EmailIsMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.NOT_MODIFIED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(PasswordIsMissingException.class)
  protected ResponseEntity<Object> handlePasswordIsMissing(PasswordIsMissingException exception) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }


  @ExceptionHandler(BadCredentialsException.class)
  protected ResponseEntity<Object> handleBadCredential(BadCredentialsException exception) {
    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(NothingToChangeException.class)
  protected ResponseEntity<Object> handleNothingToChange(NothingToChangeException exception) {
    ApiError apiError = new ApiError(HttpStatus.EXPECTATION_FAILED);
    apiError.setMessage(exception.getMessage());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
