package com.neu.edu.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neu.edu.entity.User;
import com.neu.edu.entity.dto.UserSignUp;
import com.neu.edu.exception.SchemeException;
import com.neu.edu.exception.UpdateReadOnlyFieldException;
import com.neu.edu.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/user")
@RestController
public class UserController {

  @Resource
  UserService userService;

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public Object signUp(@RequestBody @Valid UserSignUp info, Errors errors) throws SchemeException {
    if (errors.hasErrors()) {
      throw new SchemeException(
        Objects.requireNonNull(errors.getFieldError()).getField() + " is not a well-formed field");
    }
    User target = new User();
    BeanUtils.copyProperties(info, target);
    return userService.signUp(target);
  }

  @GetMapping("/self")
  public Object get() {
    return userService.getInfo();
  }

  @PutMapping("/self")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@RequestBody User info) {
    User user = userService.getInfo();
    if (StringUtils.isNotEmpty(info.getId()) && !StringUtils.equals(user.getId(), info.getId())) {
      throw new UpdateReadOnlyFieldException("Cannot update other users' information!");
    }
    // todo username validate
    // 1. unique
    // 2. validate again
    // 2.1 send an email to the old address
    user.setUsername(info.getUsername());
    userService.updateById(info);
  }

  @DeleteMapping("/remove")
  public void remove() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
//    userService.remove(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
  }

}