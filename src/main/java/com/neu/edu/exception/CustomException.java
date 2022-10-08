package com.neu.edu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author arronshentu
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {

  public CustomException(String message) {
    super(message);
  }

}
