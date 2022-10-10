package com.neu.edu.exception;

/**
 * @author arronshentu
 */
public class PermissionDeniedException extends RuntimeException {
  public PermissionDeniedException(String message) {
    super(message);
  }
}
