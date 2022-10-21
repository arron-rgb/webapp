package com.neu.edu.controller; /**
 * @author arronshentu
 */

import com.neu.edu.entity.Reminder;
import com.neu.edu.exception.CustomException;
import com.neu.edu.mapper.ReminderMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

  @GetMapping("/revert")
  public void revert(@RequestParam("token") String token) {
    // 通过token拿到userId
    // 如果verified为true 说明账号已被人修改
    // 此时根据dynamoDB中的username
    // 再次更新username
    userService.revert(token);
  }

  @Resource
  ReminderMapper reminderMapper;
  @Resource
  DynamoDbClient dynamoDbClient;


  @Value("${aws.dynamodb.table-name}")
  String tableName;

  @GetMapping("reminder/update")
  void endpoint(@RequestParam("id") String reminderId, @RequestParam("token") String token) {
    Reminder query = reminderMapper.selectById(reminderId);
    if (query == null) {
      throw new CustomException("reminder does not exist!");
    }
    Map<String, AttributeValue> queryMap = new HashMap<>();
    queryMap.put("token", AttributeValue.builder().s(token).build());
    GetItemResponse response = dynamoDbClient.getItem((req) -> {
      req.tableName(tableName);
      req.key(queryMap);
    });
    if (!response.hasItem()) {
      throw new CustomException("token does not exist!");
    }
    query.setSent(1);
    reminderMapper.updateById(query);
  }
}
