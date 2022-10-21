package com.neu.edu.service;

import com.neu.edu.entity.Message;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.Map;

/**
 * @author arronshentu
 */
public interface AwsService {
  Map<String, String> upload(MultipartFile file) throws IOException;

  void delete(String key, boolean throwEx);

  String writeToken(String userId, String username);

  void sendSns(String to, String token, String subject, String newEmail);

  String getUserIdFromToken(String token);

  void deleteToken(String token);

  boolean isValid(String token);

  void publishMessage(Message message);

  boolean putItem(Map<String, AttributeValue> map);

  Map<String, AttributeValue> getFullMap(String token, Map<String, AttributeValue> map);

  Map<String, AttributeValue> query(String token);

  AttributeValue getValue(String token, String key);
}
