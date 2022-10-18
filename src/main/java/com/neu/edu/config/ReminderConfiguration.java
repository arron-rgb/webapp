package com.neu.edu.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.edu.entity.Message;
import com.neu.edu.entity.Reminder;
import com.neu.edu.entity.Task;
import com.neu.edu.entity.User;
import com.neu.edu.mapper.ReminderMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sns.SnsClient;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author arronshentu
 */
@Slf4j
@Configuration
public class ReminderConfiguration {

  @Resource
  SnsClient sns;
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

  @Value("${aws.sns.topic-name}")
  String topicName;
  ObjectMapper mapper = new ObjectMapper();

  void buildMessage(Reminder reminder) {
    String taskId = reminder.getTaskId();
    Task task = taskMapper.selectById(taskId);
    if (task == null) {
      reminderMapper.deleteById(reminder);
      log.debug("Task was deleted. Delete reminder as well.");
      return;
    }
    // TODO 直接在配置文件里配置arn也行
//    CreateTopicResponse result = sns.createTopic((req) -> req.name(topicName));

    String userId = task.getUserId();
    User user = userMapper.selectById(userId);
    Message message = new Message();
    message.setSubject("Reminder");
    message.setFrom(from);
    message.setTo(user.getUsername());
    // TODO 配置一个入口，让lambda在发送邮件以后把这个reminder的sent设置为1
    // 或者 把发送过的reminder存到dynamoDB里，在定时job里轮询reminder的时候，检查一下是否已发送。已发送就我这里更新reminder的sent。
    // 没发送或者没这个reminder就按没发送的逻辑处理
    message.setEndpoint("TODO");
    message.setContent("TODO");
//    sns.publish((req) -> {
//      req.topicArn(result.topicArn());
    try {
      String body = mapper.writeValueAsString(message);
//        req.message(body);
      log.info(body);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
//    });

  }


}
