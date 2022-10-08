package com.neu.edu.controller; /**
 * @author arronshentu
 */

import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author arronshentu
 */
@RestController("/")
public class IndexController {

  @GetMapping("")
  public Result<Object> index() {
    return Result.buildOkData("Hello");
  }

  @Resource
  UserService userService;

  @GetMapping("verify")
  public Result<Object> verify(String token) {
    userService.verify(token);
    return Result.buildOkData("success");
  }

}
