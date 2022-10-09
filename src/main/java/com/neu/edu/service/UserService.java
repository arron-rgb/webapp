package com.neu.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.edu.entity.TodoList;
import com.neu.edu.entity.User;

import java.util.List;

/**
 * @author arronshentu
 */
public interface UserService extends IService<User> {
  void verify(String token);

  /**
   * create a new user and default list
   *
   * @param detail SignUpDetails
   * @return User with inserted id
   * @see com.neu.edu.entity.dto.UserSignUp
   */
  User signUp(User detail);

  User getInfo();

  List<TodoList> getTodoLists();

  TodoList updateTodoList(TodoList list);
}

