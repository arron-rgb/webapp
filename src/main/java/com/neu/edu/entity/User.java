package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author arronshentu
 */
@Data
@TableName("t_user")
public class User {

  private String password;
  private String firstName;
  private String middleName;
  private String lastName;
  private String email;
  private String username;
  private boolean verified;
  private String id;

  public boolean isVerified() {
    return this.verified;
  }

}
