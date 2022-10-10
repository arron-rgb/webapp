package com.neu.edu.controller;

import com.neu.edu.entity.Task;
import com.neu.edu.entity.dto.TaskDto;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.exception.SchemeException;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/task")
@RestController
public class TaskController {

  @Resource
  TaskMapper taskMapper;
  @Resource
  UserService userService;

  @GetMapping()
  Result<Object> getList() {
    return Result.buildOkData(userService.getTodoLists());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Result<Object> create(@RequestBody @Valid TaskDto taskDto, Errors errors) throws SchemeException {

    // step1: list id summary task due priority
    if (!userService.hasPermissionToEditList(taskDto.getListId())) {
      throw new PermissionDeniedException("");
    }
    if (errors.hasErrors()) {
      throw new SchemeException(
        Objects.requireNonNull(errors.getFieldError()).getField() + " is not a well-formed field");
    }
    Task task = new Task();
    taskDto.setId("");
    BeanUtils.copyProperties(taskDto, task);
    task.setUserId(userService.getInfo().getId());
    taskMapper.insert(task);
    return Result.buildOkData(taskDto);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void update(@RequestBody @Valid TaskDto taskDto, Errors errors) throws SchemeException {
    if (!userService.hasPermissionToEditList(taskDto.getListId())) {
      throw new PermissionDeniedException("");
    }
    if (!userService.hasPermissionToEditTask(taskDto.getId())) {
      throw new PermissionDeniedException("");
    }
    if (errors.hasErrors()) {
      throw new SchemeException(
        Objects.requireNonNull(errors.getFieldError()).getField() + " is not a well-formed field");
    }
    Task task = taskMapper.selectById(taskDto.getId());
    BeanUtils.copyProperties(taskDto, task);
    taskMapper.updateById(task);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@RequestParam String taskId) throws SchemeException {
    userService.deleteTask(taskId);
  }

}
