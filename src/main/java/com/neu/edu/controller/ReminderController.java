package com.neu.edu.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Reminder;
import com.neu.edu.exception.CustomException;
import com.neu.edu.mapper.ReminderMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author arronshentu
 */
@RestController
@Slf4j
@RequestMapping("/${api-version}/reminder")
public class ReminderController {

  @Value("${limit.reminder.amount}")
  Long maxReminderAmount;

  @Resource
  ReminderMapper reminderMapper;
  @Resource
  UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Result<Object> createReminder(@RequestBody Reminder reminder) {
    userService.hasPermissionToEditTask(reminder.getTaskId());
    if (reminderMapper.selectCount(Wrappers.<Reminder>lambdaQuery().eq(Reminder::getTaskId, reminder.getTaskId())) >= maxReminderAmount) {
      throw new CustomException("");
    }
    reminderMapper.insert(reminder);
    return Result.buildOkData(reminder);
  }

  @GetMapping
  Result<Object> getAllReminders(@RequestParam("taskId") String taskId) {
    userService.hasPermissionToEditTask(taskId);
    return Result.buildOkData(reminderMapper.selectList(Wrappers.<Reminder>lambdaQuery().eq(Reminder::getTaskId, taskId)));
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@RequestParam("id") String reminderId) {
    Reminder query = reminderMapper.selectById(reminderId);
    if (query == null) {
      throw new CustomException("");
    }
    userService.hasPermissionToEditTask(query.getTaskId());
    reminderMapper.deleteById(reminderId);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.CREATED)
  Result<Object> updateReminder(@RequestBody Reminder reminder) {
    Reminder query = reminderMapper.selectById(reminder.getId());
    if (query == null) {
      throw new CustomException("");
    }
    userService.hasPermissionToEditTask(reminder.getTaskId());
    reminderMapper.updateById(reminder);
    return Result.buildOkData(reminder);
  }


}
