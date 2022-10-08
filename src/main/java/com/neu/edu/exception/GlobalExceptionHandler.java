package com.neu.edu.exception;

import com.neu.edu.util.Result;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author arronshentu
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthenticationServiceException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Result<String> unauthorized(Exception e) {
    return Result.buildFail(e.getMessage());
  }

  @ExceptionHandler(MailExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<Object> mailExistsException(MailExistsException e) {
    return Result.buildFailData(e.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public Result<Object> runtime(RuntimeException e) {
    e.printStackTrace();
    return Result.buildFailData("Unknown Exception");
  }

  @ExceptionHandler(CustomException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Result<Object> customException(CustomException e) {
    return Result.buildFailData(e.getMessage());
  }

}
