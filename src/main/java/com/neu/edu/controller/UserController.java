package com.neu.edu.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.neu.edu.entity.User;
import com.neu.edu.entity.dto.UserSignUp;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.SchemeException;
import com.neu.edu.exception.UpdateReadOnlyFieldException;
import com.neu.edu.mapper.UserMapper;
import com.neu.edu.service.AwsService;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
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

  @Resource
  UserMapper mapper;

  @Resource
  AwsService awsService;

  @PostMapping("")
  @ResponseStatus(HttpStatus.CREATED)
  public Object signUp(@RequestBody @Valid UserSignUp info, Errors errors) throws SchemeException {
    if (errors.hasErrors()) {
      throw new SchemeException(
        Objects.requireNonNull(errors.getFieldError()).getField() + " is not a well-formed field");
    }
    User target = new User();
    BeanUtils.copyProperties(info, target);
    // todo delete this line
    target.setVerified(true);
    return userService.signUp(target);
  }

  @GetMapping("/self")
  public Result<Object> get() {
    return Result.buildOkData(userService.getInfo());
  }

  @PutMapping("/self")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@RequestBody User info) {
    User user = userService.getInfo();
    if (StringUtils.isNotEmpty(info.getId()) && !StringUtils.equals(user.getId(), info.getId())) {
      throw new UpdateReadOnlyFieldException("Cannot update other users' information!");
    }
    // 1. unique
    User tempUser = mapper.findByUsername(info.getUsername());
    if (tempUser != null) {
      throw new CustomException("new email address is invalid!");
    }
    if (Objects.equals(info.getUsername(), user.getUsername())) {
      throw new CustomException("nothing changed");
    }

    String token = awsService.writeToken(user.getId(), user.getUsername());
    awsService.sendSns(user.getUsername(), token, "ChangeUserEmail", info.getUsername());


    // email the old address
    // todo dynamodb中的token过期以后 email应该回滚成原先的
    user.setUsername(info.getUsername());
    user.setVerified(false);
    userService.updateById(user);
  }

  @DeleteMapping("/remove")
  public void remove() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
//    userService.remove(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
  }

}
