package com.neu.edu.controller;

import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/task")
@RestController
public class TaskController {

  @Resource
  UserService userService;

  @GetMapping()
  Result<Object> getList() {
    return Result.buildOkData(userService.getTodoLists());
  }

}
