package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author arronshentu
 */
@Data
@TableName("t_user")
public class User {

  @JsonIgnore
  private String password;
  private String firstName;
  private String middleName;
  private String lastName;
  private String username;
  @JsonIgnore
  private boolean verified;
  private String id;

  public boolean isVerified() {
    return this.verified;
  }

}
