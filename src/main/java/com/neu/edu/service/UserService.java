package com.neu.edu.service;

import com.neu.edu.entity.User;

/**
 * @author arronshentu
 */
public interface UserService {
  void verify(String token);

  User signUp(User detail);

  User getInfo();
}

