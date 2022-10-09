package com.neu.edu.controller;

import com.neu.edu.entity.TodoList;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/list")
public class ListController {
  @Resource
  UserService userService;

  @GetMapping()
  Result<Object> getList() {
    return Result.buildOkData(userService.getTodoLists());
  }

  @PutMapping
  void updateList(TodoList list) {
    userService.updateTodoList(list);
  }
}
