package com.neu.edu.util;

/*
 * @author arronshentu
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * @author arronshentu
 */
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class Result<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter
  @Setter
  private int code = Constants.SUCCESS;

  @Getter
  @Setter
  private T data;

  private String[] messages = {};

  public Result() {
    super();
  }

  public Result(T data) {
    super();
    this.data = data;
  }

  public Result(String... msg) {
    super();
    this.messages = msg;
  }

  public Result(T data, String... msg) {
    super();
    this.data = data;
    this.messages = msg;
  }

  public Result(T data, int code, String... msg) {
    super();
    this.data = data;
    this.code = code;
    this.messages = msg;
  }

  public Result(Throwable e) {
    super();
    setMessage(e.getMessage());
    this.code = Constants.FAIL;
  }

  public static Result<String> buildOk(String... messages) {
    return new Result<>(messages);
  }

  public static <T> Result<Object> buildOkData(T data, String... messages) {
    return new Result<>(data, messages);
  }

  public static <T> Result<Object> buildFailData(T data, String... messages) {
    return new Result<>(data, Constants.FAIL, messages);
  }

  public static <T> Result<String> buildFail(String... messages) {
    return new Result<>(null, Constants.FAIL, messages);
  }

  public static <T> Result<Object> build(T data, int code, String... messages) {
    return new Result<>(data, code, messages);
  }

  public static Result<String> build(int code, String... messages) {
    return new Result<>(null, code, messages);
  }

  public String getMessage() {
    return readMessages();
  }

  public void setMessage(String message) {
    addMessage(message);
  }

  public String readMessages() {
    StringBuilder sb = new StringBuilder();
    for (String message : messages) {
      sb.append(message);
    }
    return sb.toString();
  }

  public void addMessage(String message) {
    this.messages = ObjectUtils.addObjectToArray(messages, message);
  }

}
