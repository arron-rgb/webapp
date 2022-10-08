package com.neu.edu.service.impl;

import com.neu.edu.entity.CustomUserDetails;
import com.neu.edu.entity.User;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.MailExistsException;
import com.neu.edu.mapper.UserMapper;
import com.neu.edu.service.UserService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author arronshentu
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
  @Resource
  PasswordEncoder passwordEncoder;
  @Resource
  UserMapper mapper;

  @Override
  public void verify(String token) {

  }

  @Override
  public User signUp(User detail) {
    User user = mapper.findByUsername(detail.getUsername());
    if (!Objects.isNull(user)) {
      throw new MailExistsException("Username exists!");
    }
    user = new User();
    user.setPassword(passwordEncoder.encode(detail.getPassword()));
    user.setUsername(detail.getUsername());
    user.setLastName(detail.getLastName());
    user.setFirstName(detail.getFirstName());
    mapper.insert(user);

    return user;
  }

  @Override
  public User getInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!StringUtils.hasText(auth.getName()) || "anonymousUser".equals(auth.getName())) {
      throw new AuthenticationServiceException("Unauthorized");
    }
    return checkIfExist(auth.getName());
  }

  public User checkIfExist(String username) {
    if (!StringUtils.hasText(username)) {
      throw new CustomException("Username cannot be blank");
    }
    User one = mapper.findByUsername(username);
    if (one == null) {
      throw new CustomException("User does not exist");
    }
    return one;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return new CustomUserDetails(checkIfExist(s));
  }
}
