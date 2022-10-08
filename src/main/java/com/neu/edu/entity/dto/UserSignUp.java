package com.neu.edu.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author arronshentu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUp {

  private String firstName;
  private String lastName;
  @NotNull
  @Email
  private String username;
  @NotNull
  private String password;

}

