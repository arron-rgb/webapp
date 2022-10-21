package com.neu.edu.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Message;
import com.neu.edu.entity.Reminder;
import com.neu.edu.entity.Task;
import com.neu.edu.entity.User;
import com.neu.edu.mapper.ReminderMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.mapper.UserMapper;
import com.neu.edu.service.AwsService;
import com.neu.edu.util.ApplicationConfig;
import com.neu.edu.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.neu.edu.util.Constants.REMINDER_SUBJECT;

/**
 * @author arronshentu
 */
@Slf4j
@Configuration
public class ReminderConfiguration {

  @Resource
  AwsService awsService;
  @Resource
  UserMapper userMapper;
  @Resource
  TaskMapper taskMapper;
  @Resource
  ReminderMapper reminderMapper;

  @Value("${aws.sns.from}")
  String from;

  @Scheduled(fixedDelay = 30000)
  void reminder() {
    // 获取未发送、且reminder时间在10s内的发布
    // now - reminder.getReminderTime <= 10
    // reminder.getReminderTime >= now - 10s
    LambdaQueryWrapper<Reminder> queryWrapper = Wrappers.<Reminder>lambdaQuery().eq(Reminder::getSent, 0);
    queryWrapper = queryWrapper.and(q -> q.gt(Reminder::getReminderTime, LocalDateTime.now().minusSeconds(10L)).or().lt(Reminder::getReminderTime, LocalDateTime.now()));
    List<Reminder> reminders = reminderMapper.selectList(queryWrapper);
    reminders.forEach(this::buildMessage);
  }


  void buildMessage(Reminder reminder) {
    String taskId = reminder.getTaskId();
    Task task = taskMapper.selectById(taskId);
    if (task == null) {
      reminderMapper.deleteById(reminder);
      log.debug("Task was deleted. Delete reminder as well.");
      return;
    }

    String userId = task.getUserId();
    User user = userMapper.selectById(userId);
    if (user == null) {
      reminderMapper.deleteById(reminder);
      log.error("User was deleted. Delete reminder as well.");
      return;
    }
    Message message = new Message().setSubject(REMINDER_SUBJECT).setFrom(from).setTo(user.getUsername()).setReminderId(reminder.getId());
    message.setContent("This is a reminder notification. You have an uncompleted task.");
    String token = TokenProvider.generate();
    message.setToken(token);
    message.setEndpoint(ApplicationConfig.getEndpoint("server.reminder-path") + String.format("?id=%s&token=%s", reminder.getId(), token));
    awsService.publishMessage(message);
    awsService.putItem(awsService.getFullMap(token, new HashMap<>()));
  }


}
