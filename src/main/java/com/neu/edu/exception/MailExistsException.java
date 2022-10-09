package com.neu.edu.exception;

/**
 * @author arronshentu
 */

public class MailExistsException extends RuntimeException {

  public MailExistsException(String message) {
    super(message);
  }

}
